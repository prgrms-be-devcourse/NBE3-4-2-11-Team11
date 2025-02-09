"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";

function CreateNoticePage() {
  const [subject, setSubject] = useState("");
  const [content, setContent] = useState("");
  const [message, setMessage] = useState("");
  const router = useRouter();

  const refreshAccessToken = async () => {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) {
      throw new Error("리프레시 토큰이 없습니다.");
    }

    const response = await fetch("/api/refresh-token", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) {
      throw new Error("토큰 갱신에 실패했습니다.");
    }

    const data = await response.json();
    localStorage.setItem("accessToken", data.accessToken);
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      let token = localStorage.getItem("accessToken");
      console.log("Token:", token);

      if (!token) {
        throw new Error("인증 토큰이 없습니다.");
      }

      let response = await fetch("/api/v1/admin/notice", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify({
          subject,
          content,
        }),
      });

      if (response.status === 401) {
        await refreshAccessToken();
        token = localStorage.getItem("accessToken");

        response = await fetch("/api/v1/admin/notice", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`,
          },
          body: JSON.stringify({
            subject,
            content,
          }),
        });
      }

      if (!response.ok) {
        throw new Error("공지사항 생성에 실패했습니다.");
      }

      const data = await response.json();
      if (data.code === "200") {
        if (window.confirm("작성 완료되었습니다!")) {
          router.push("/notice");
        }
      } else {
        throw new Error(data.message);
      }
    } catch (error) {
      setMessage(error.message);
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">공지사항 작성</h1>
      <form onSubmit={handleSubmit}>
        <div className="mb-4">
          <label className="block text-gray-700">제목</label>
          <input
            type="text"
            value={subject}
            onChange={(e) => setSubject(e.target.value)}
            className="w-full px-3 py-2 border rounded"
            required
          />
        </div>
        <div className="mb-4">
          <label className="block text-gray-700">내용</label>
          <textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
            className="w-full px-3 py-2 border rounded"
            rows={5}
            required
          />
        </div>
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          제출하기
        </button>
      </form>
      {message && <p className="mt-4 text-red-500">{message}</p>}
    </div>
  );
}

export default CreateNoticePage;
