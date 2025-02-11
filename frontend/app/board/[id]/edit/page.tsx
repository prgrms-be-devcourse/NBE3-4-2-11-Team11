"use client";

import { useState, useEffect } from 'react';
import { getPostById, updatePost } from '../../../../lib/board';
import { useRouter } from 'next/navigation';
import Header from '../../../../components/Header';
import { useAuthStore } from '@/store/authStore';  // 로그인 상태 가져오기

const EditPostPage = ({ params }: { params: { id: string } }) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const router = useRouter();
  const { isLoggedIn } = useAuthStore();  // 로그인 상태 확인

  useEffect(() => {
    const fetchPost = async () => {
      const post = await getPostById(Number(params.id));
      setTitle(post.title);
      setContent(post.content);
    };
    fetchPost();
  }, [params.id]);

  const handleSubmit = async () => {
    if (!isLoggedIn) {
      alert('로그인 후 게시글을 수정할 수 있습니다.');
      router.push('/login');
      return;
    }

    try {
      await updatePost(Number(params.id), { title, content });
      router.push(`/board/${params.id}`);  // 수정 후 상세 페이지로 이동
    } catch (error) {
      console.error('게시글 수정 실패:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <Header />
      <main className="max-w-xl mx-auto mt-8">
        <h1 className="text-2xl font-bold mb-4">게시글 수정</h1>
        <input
          type="text"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          className="w-full p-2 mb-4 border rounded"
        />
        <textarea
          value={content}
          onChange={(e) => setContent(e.target.value)}
          className="w-full p-2 mb-4 border rounded h-40"
        />
        <button
          onClick={handleSubmit}
          className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
        >
          수정하기
        </button>
      </main>
    </div>
  );
};

export default EditPostPage;
