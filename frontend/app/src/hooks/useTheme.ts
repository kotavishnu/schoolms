import { useEffect, useState } from 'react';

type Theme = 'light' | 'dark';

export function useTheme() {
  // Default to light mode (matching reference design)
  const [theme, setTheme] = useState<Theme>('light');

  useEffect(() => {
    // Ensure light mode on mount
    const root = window.document.documentElement;
    root.classList.remove('dark');

    // Check if user has a saved preference
    const savedTheme = localStorage.getItem('theme') as Theme | null;
    if (savedTheme) {
      setTheme(savedTheme);
      if (savedTheme === 'dark') {
        root.classList.add('dark');
      }
    }
  }, []);

  const toggleTheme = () => {
    const root = window.document.documentElement;
    const newTheme: Theme = theme === 'light' ? 'dark' : 'light';

    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);

    if (newTheme === 'dark') {
      root.classList.add('dark');
    } else {
      root.classList.remove('dark');
    }
  };

  return { theme, toggleTheme, isDark: theme === 'dark' };
}
