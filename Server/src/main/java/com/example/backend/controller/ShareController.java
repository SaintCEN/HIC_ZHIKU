package com.example.backend.controller;

import com.example.backend.dto.response.ApiResponse;
import com.example.backend.dto.response.PageResponse;
import com.example.backend.entity.Share;
import com.example.backend.service.ShareService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/senior-shares")
public class ShareController {

    private final ShareService shareService;

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    public ShareController(ShareService shareService) {
        this.shareService = shareService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<Share>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        PageResponse<Share> pr = shareService.list(page, size, search);
        return ResponseEntity.ok(ApiResponse.success(pr));
    }

    @GetMapping("/test-direct")
    public ResponseEntity<ApiResponse<List<Share>>> testDirect() {
        List<Share> shares = shareService.testDirectQuery();
        return ResponseEntity.ok(ApiResponse.success(shares));
    }

    @GetMapping("/test-param")
    public ResponseEntity<ApiResponse<List<Share>>> testParam(@RequestParam String category) {
        List<Share> shares = shareService.testParameterQuery(category);
        return ResponseEntity.ok(ApiResponse.success(shares));
    }

    @GetMapping("/test-param-limit")
    public ResponseEntity<ApiResponse<List<Share>>> testParamLimit(@RequestParam String category) {
        List<Share> shares = shareService.testParameterWithLimitQuery(0, 9, category);
        return ResponseEntity.ok(ApiResponse.success(shares));
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Share>> get(@PathVariable Long id,
                                                  @RequestParam(defaultValue = "true") boolean increaseViewCount) {
        Share share = shareService.getById(id, increaseViewCount);
        if (share == null) {
            return ResponseEntity.ok(ApiResponse.error("分享不存在"));
        }
        // 若 textUrl 为空，按约定位置查找 Markdown 文件，但不修改对象本身（返回给前端的 textUrl 仍可能为空）
        // 前端会根据 textUrl 是否以 http 或 /uploads/ 开头自行加载
        return ResponseEntity.ok(ApiResponse.success(share));
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<Void>> addView(@PathVariable Long id) {
        shareService.getById(id, true);
        return ResponseEntity.ok(ApiResponse.success());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Share>> create(@RequestBody Share share) {
        try {
            Share createdShare = shareService.create(share);
            return ResponseEntity.ok(ApiResponse.success(createdShare));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("创建分享失败: " + e.getMessage()));
        }
    }
}


