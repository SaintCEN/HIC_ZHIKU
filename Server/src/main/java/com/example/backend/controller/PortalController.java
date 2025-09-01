package com.example.backend.controller;

import com.example.backend.entity.Announcement;
import com.example.backend.entity.Event;
import com.example.backend.service.AnnouncementService;
import com.example.backend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/portal")
@RequiredArgsConstructor
public class PortalController {

    private final AnnouncementService announcementService;
    private final EventService eventService;

    @Value("${file.upload.path:uploads}")
    private String uploadPath;

    /**
     * 门户首页数据：目前仅组装 announcements 字段，其他字段可后续扩展
     */
    @GetMapping("/home")
    public ResponseEntity<?> home() {
        Map<String, Object> data = new HashMap<>();
        List<Announcement> latest = announcementService.latest(2);
        data.put("announcements", latest);
        List<Event> latestEvents = eventService.latest(2);
        data.put("events", latestEvents);
        data.put("resources", List.of());
        return ResponseEntity.ok(data);
    }

    /**
     * 公告列表
     */
    @GetMapping("/announcements")
    public ResponseEntity<?> announcements(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        List<Announcement> list = announcementService.listAll(page, size);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 0);
        resp.put("message", "OK");
        resp.put("data", list);
        return ResponseEntity.ok(resp);
    }

    /**
     * 公告详情
     */
    @GetMapping("/announcements/{id}")
    public ResponseEntity<?> announcementDetail(@PathVariable Long id, 
                                               @RequestParam(defaultValue = "false") boolean increaseView) {
        Announcement a = announcementService.getById(id, increaseView);
        if (a == null) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "message", "公告不存在"));
        }
        // 若内容为空，尝试使用约定位置的 Markdown 文件
        if (a.getContent() == null || a.getContent().isBlank()) {
            Path mdPath = Paths.get(uploadPath, "announcements", "contents", id + ".md");
            if (Files.exists(mdPath)) {
                a.setContent("/uploads/announcements/contents/" + id + ".md");
            }
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 0);
        resp.put("message", "OK");
        resp.put("data", a);
        return ResponseEntity.ok(resp);
    }

    /**
     * 增加公告浏览量
     */
    @PostMapping("/announcements/{id}/view")
    public ResponseEntity<?> incrementAnnouncementView(@PathVariable Long id) {
        announcementService.getById(id, true);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 0);
        resp.put("message", "OK");
        return ResponseEntity.ok(resp);
    }

    /**
     * 事件列表
     */
    @GetMapping("/events")
    public ResponseEntity<?> events(@RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String type,
                                    @RequestParam(required = false) String search) {
        List<Event> list = eventService.listAll(page, size, type, search);
        long total = eventService.count(type, search);
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 0);
        resp.put("message", "OK");
        resp.put("data", list);
        resp.put("total", total);
        return ResponseEntity.ok(resp);
    }

    /**
     * 事件详情
     */
    @GetMapping("/events/{id}")
    public ResponseEntity<?> eventDetail(@PathVariable Long id) {
        Event e = eventService.getById(id, true);
        if (e == null) {
            return ResponseEntity.status(404).body(Map.of("code", 404, "message", "活动不存在"));
        }
        // 若 textUrl 为空，尝试使用约定位置的 Markdown 文件
        if (e.getTextUrl() == null || e.getTextUrl().isBlank()) {
            Path mdPath = Paths.get(uploadPath, "events", "contents", id + ".md");
            if (Files.exists(mdPath)) {
                e.setTextUrl("/uploads/events/contents/" + id + ".md");
            }
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("code", 0);
        resp.put("message", "OK");
        resp.put("data", e);
        return ResponseEntity.ok(resp);
    }
}