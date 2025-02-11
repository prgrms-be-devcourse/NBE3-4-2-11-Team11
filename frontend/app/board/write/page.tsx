"use client";

import { useState } from 'react';
import { createPost } from '../../../lib/board';
import { useRouter } from 'next/navigation';
import { useAuthStore } from '@/store/authStore';  // 로그인 상태 가져오기
import { getAccessToken } from '@/utils/token';    // 토큰 가져오기
import { decodeJWT } from '@/utils/decodeJWT';     // JWT 디코딩
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

const WritePostPage = () => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const router = useRouter();
  const { isLoggedIn } = useAuthStore();  

  const handleSubmit = async () => {
    if (!isLoggedIn) {
      alert('로그인 후 게시글을 작성할 수 있습니다.');
      router.push('/login');  
      return;
    }

    try {
      // 토큰에서 이메일 추출
      const token = getAccessToken();
      if (!token) {
        alert('로그인 정보가 올바르지 않습니다.');
        return;
      }

      const decoded: any = decodeJWT(token);
      const email = decoded?.sub;  // 이메일 정보는 JWT의 sub 필드에 있음

      if (!email) {
        alert('사용자 이메일 정보를 찾을 수 없습니다.');
        return;
      }

      // 이메일 포함하여 게시글 작성 요청
      await createPost({ title, content, email });
      router.push('/board');  // 작성 완료 후 목록 페이지로 이동
    } catch (error: any) {
      console.error('게시글 작성 실패:', error);
      alert(`게시글 작성 중 오류가 발생했습니다: ${error.message}`);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <main className="max-w-xl mx-auto mt-8">
        <h1 className="text-2xl font-bold mb-4">게시글 작성</h1>

        <input
          type="text"
          placeholder="제목을 입력하세요"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full p-2 mb-4 border rounded"
        />

        <textarea
          placeholder="내용을 마크다운 형식으로 작성하세요"
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="w-full p-2 mb-4 border rounded h-40"
        />

        <button
          onClick={handleSubmit}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          작성하기
        </button>

        <div className="mt-8">
          <h2 className="text-xl font-semibold mb-2">미리보기</h2>
          <div className="p-4 border rounded bg-white">
            <ReactMarkdown remarkPlugins={[remarkGfm]}>
              {content || '내용을 입력하면 마크다운 형식으로 미리보기가 표시됩니다.'}
            </ReactMarkdown>
          </div>
        </div>
      </main>
    </div>
  );
};

export default WritePostPage;
