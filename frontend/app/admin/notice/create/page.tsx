"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";

const CreateNoticePage = () => {
  const [subject, setSubject] = useState("");
  const [content, setContent] = useState("");
  const [message, setMessage] = useState("");
  const router = useRouter();

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      // HTTP-only 쿠키 기반 인증이므로 토큰을 직접 다루지 않습니다.
      const response = await axios.post(
        "/api/v1/admin/notice",
        { subject, content },
        { withCredentials: true }
      );

      if (response.status === 200) {
        alert("공지사항이 성공적으로 생성되었습니다!");
        router.push("/admin/notice/manage");
      } else {
        throw new Error("공지사항 생성에 실패했습니다.");
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
};

export default CreateNoticePage;
