import { defineStore } from 'pinia';
import { useStorage } from '@vueuse/core';

export const useThemeStore = defineStore('theme', {
  state: () => ({
    darkMode: useStorage('dark-mode', 
      window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches
    ),
  }),
  actions: {
    toggleDarkMode() {
      this.darkMode = !this.darkMode;
      this.updateHtmlClass();
    },
    updateHtmlClass() {
      if (this.darkMode) {
        document.documentElement.classList.add('dark');
      } else {
        document.documentElement.classList.remove('dark');
      }
    },
    init() {
      this.updateHtmlClass();
    }
  }
});
