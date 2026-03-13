# ClojureScript Mobile App

A minimal, mobile-friendly ClojureScript application designed to be deployed on GitHub Pages. This project uses only the ClojureScript standard library with no additional dependencies.

## Features

- ✨ Minimal dependencies (only ClojureScript standard library)
- 📱 Mobile-first responsive design
- 🚀 Optimized for GitHub Pages deployment
- 🎨 Clean, modern UI
- ⚡ Fast build with advanced compilation

## Prerequisites

- [Clojure CLI](https://clojure.org/guides/install_clojure) (version 1.10.3 or higher)
- Java Development Kit (JDK) 8 or higher

## Project Structure

```
.
├── src/
│   └── app/
│       └── core.cljs          # Main application code
├── public/
│   ├── index.html             # HTML entry point
│   ├── css/
│   │   └── style.css          # Styling (mobile-first)
│   └── js/                    # Compiled JavaScript (generated)
├── deps.edn                   # Dependencies configuration
├── build.sh                   # Build script
└── README.md
```

## Getting Started

### Development Mode

For development with live reloading:

```bash
clojure -M:watch
```

This will:
- Watch for file changes in the `src` directory
- Automatically recompile on changes
- Start a REPL
- Serve the app on http://localhost:9000

### Production Build

To build for production:

```bash
./build.sh
```

Or manually:

```bash
clojure -M:build
```

This creates an optimized, minified JavaScript file at `public/js/main.js` using advanced compilation.

## Deployment to GitHub Pages

### Option 1: Manual Deployment

1. Build the project:
   ```bash
   ./build.sh
   ```

2. The `public` directory contains your complete app

3. Configure GitHub Pages:
   - Go to your repository settings
   - Navigate to "Pages"
   - Set source to deploy from a branch
   - Select the branch and `/public` folder
   - Save

### Option 2: Automated GitHub Actions (Recommended)

The project includes a GitHub Actions workflow that automatically builds and deploys to GitHub Pages on every push to the main branch. See `.github/workflows/deploy.yml`.

## Development

### Adding New Features

1. Edit `src/app/core.cljs` to add new functionality
2. The namespace uses standard ClojureScript functions and the browser's DOM API
3. Test your changes in development mode with `clojure -M:watch`
4. Build for production when ready

### File Organization

- Keep your ClojureScript source files in `src/app/`
- Static assets (HTML, CSS, images) go in `public/`
- Build output goes to `public/js/` (gitignored)

## Customization

### Changing the App

Edit `src/app/core.cljs` to customize the application logic. The current example includes:
- A simple click counter
- DOM manipulation using standard ClojureScript
- Basic state management with atoms

### Styling

Edit `public/css/style.css` to customize the appearance. The current styles are:
- Mobile-first responsive
- Modern gradient background
- Clean, accessible button styles

### HTML Structure

Edit `public/index.html` to modify the page structure or metadata.

## Technologies Used

- **ClojureScript**: A Clojure to JavaScript compiler
- **Clojure CLI**: For dependency management and build tooling
- **GitHub Pages**: For hosting the static site

## Browser Compatibility

The app works in all modern browsers:
- Chrome/Edge (latest)
- Firefox (latest)
- Safari (latest)
- Mobile browsers (iOS Safari, Chrome Mobile)

## License

This is a starter template - use it however you like!

## Next Steps

Some ideas for expanding this app:

- Add more interactive features
- Implement local storage for persistence
- Create multiple views/pages
- Add more sophisticated state management
- Integrate with APIs
- Add animations and transitions

Happy coding! 🎉
