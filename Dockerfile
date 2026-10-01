FROM node:22-alpine

WORKDIR /app

# Copy dependency specifications
COPY package.json package-lock.json* ./

# Copy all application assets and server scripts
COPY . .

# Environment configuration for Cloud Run
ENV PORT=8080
ENV NODE_ENV=production

EXPOSE 8080

CMD ["node", "server.ts"]
