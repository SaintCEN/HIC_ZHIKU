/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'primary': '#E41E2B', // 华为红色
        'secondary': '#00A0E9', // 蓝色
        'dark': '#181818',
        'light': '#F5F5F5',
      },
      fontFamily: {
        sans: ['HarmonyOS Sans SC', 'Arial', 'sans-serif'],
      },
    },
  },
  plugins: [],
} 