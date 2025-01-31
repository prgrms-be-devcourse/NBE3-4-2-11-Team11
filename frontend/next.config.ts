import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */

  images: {
    remotePatterns: [
      {
        protocol: "https",
        hostname: "i.postimg.cc", // PostImage 도메인 추가
      },
    ],
  },
};

export default nextConfig;
