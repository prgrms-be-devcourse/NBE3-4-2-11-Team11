"use client"; // 클라이언트 컴포넌트로 설정

import { useParams, useRouter } from 'next/navigation';
import { useState } from 'react';
import axios from 'axios';

const ReplyCreatePage = () => {
  const { inquiryId } = useParams(); // URL에서 id 가져오기
  const router = useRouter();
  const [replyContent, setReplyContent] = useState(''); // 답변 내용 상태
  const [message, setMessage] = useState(''); // 성공 메시지 상태
  const [error, setError] = useState(''); // 오류 메시지 상태

  const handleReplySubmit = async (e: React.FormEvent) => {
    e.preventDefault(); // 기본 폼 제출 방지
    try {
      const token = localStorage.getItem('accessToken'); // 토큰 가져오기
      const response = await axios.post(`/api/v1/admin/inquiries/${inquiryId}/reply`, {
        content: replyContent,
      }, {
        headers: {
          Authorization: `Bearer ${token}`, // 인증 헤더 추가
        },
      });

      if (response.status === 200) {
        setMessage("답변이 성공적으로 등록되었습니다!");
        router.push(`/inquiry/${inquiryId}`); // 문의 상세 페이지로 리다이렉트
      } else {
        throw new Error("답변 등록에 실패했습니다.");
      }
    } catch (err) {
      console.error('Error submitting reply:', err);
      setError(err.message); // 오류 메시지 설정
    }
  };

  return (
    <div className="max-w-2xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-4">답변 등록</h1>
      <form onSubmit={handleReplySubmit}>
        <div className="mb-4">
          <label className="block text-gray-700">답변 내용</label>
          <textarea
            value={replyContent}
            onChange={(e) => setReplyContent(e.target.value)}
            className="w-full px-3 py-2 border rounded"
            rows={5}
            required
          />
        </div>
        <button
          type="submit"
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          등록하기
        </button>
      </form>
      {message && <p className="mt-4 text-green-500">{message}</p>}
      {error && <p className="mt-4 text-red-500">{error}</p>}
    </div>
  );
};

export default ReplyCreatePage;
