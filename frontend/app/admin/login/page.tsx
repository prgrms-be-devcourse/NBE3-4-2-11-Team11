"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

export default function AdminLoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const router = useRouter();

  const handleAdminLogin = async () => {
    setLoading(true);
    setError("");

    try {
      const response = await fetch("http://localhost:8080/api/v1/admin/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "로그인 실패");
      }

      // 헤더에서 토큰 가져오기
      const accessToken = response.headers
        .get("Authorization")
        ?.replace("Bearer ", "");
      const refreshToken = response.headers.get("Refresh-Token");

      if (!accessToken || !refreshToken) {
        throw new Error("백엔드에서 토큰을 받지 못했습니다.");
      }

      // 로컬 스토리지에 토큰 저장
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);

      // 대시보드 페이지로 이동
      router.push("/admin/dashboard");
    } catch (err: any) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
      <h1 className="text-2xl font-bold mb-6">관리자 로그인</h1>
      {error && (
        <div className="bg-red-500 text-white px-4 py-2 rounded-md mb-4">
          ⚠️ {error}
        </div>
      )}
      <input
        type="text"
        placeholder="아이디"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
        className="w-64 px-4 py-2 mb-4 border rounded-md"
      />
      <input
        type="password"
        placeholder="비밀번호"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
        className="w-64 px-4 py-2 mb-4 border rounded-md"
      />
      <button
        onClick={handleAdminLogin}
        disabled={loading}
        className="bg-blue-600 text-white px-6 py-3 rounded-lg shadow-md hover:bg-blue-700 w-64"
      >
        {loading ? "로그인 중..." : "관리자 로그인"}
      </button>
    </div>
  );
}
