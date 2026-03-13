#!/bin/bash

# Build script for ClojureScript app

set -e

echo "Building ClojureScript app..."

# Clean previous build
rm -rf public/js/main.js
rm -rf out

# Build with advanced compilation for production
clojure -M:build

echo "Build complete! Output: public/js/main.js"
echo "You can now deploy the 'public' directory to GitHub Pages."
