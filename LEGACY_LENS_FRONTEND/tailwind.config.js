/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        brand: {
          400: '#6b8aff',
          500: '#4f6ef7',
          600: '#3b5ce6',
        },
        surface: {
          900: '#080c14',
          800: '#0d1220',
          700: '#111827',
          600: '#1a2235',
        },
        accent: {
          cyan:    '#22d3ee',
          violet:  '#a78bfa',
          emerald: '#34d399',
          amber:   '#fbbf24',
        }
      },
      fontFamily: {
        display: ['"Syne"', 'sans-serif'],
        body:    ['"DM Sans"', 'sans-serif'],
        mono:    ['"JetBrains Mono"', 'monospace'],
      },
      boxShadow: {
        'glow-sm': '0 0 20px rgba(79,110,247,.2)',
        'glow-md': '0 0 40px rgba(79,110,247,.25)',
      },
    },
  },
  plugins: [],
}