"use client";

import { useState, useEffect } from 'react';
import { getPostById, updatePost } from '../../../../lib/board';
import { useRouter } from 'next/navigation';
import Header from '../../../../components/Header';
import { useAuthStore } from '@/store/authStore';
import { getAccessToken } from '@/utils/token';
import { jwtDecode } from 'jwt-decode';

// Post 타입에 작성자 닉네임 추가
interface Post {
  id: number;
  title: string;
  content: string;
  nickname: string;  // 작성자 닉네임
}

// JWT 토큰에서 닉네임 추출 함수
const extractNicknameFromToken = (): string | null => {
  const token = getAccessToken();
  if (!token) return null;

  try {
    const decoded: { nickname: string } = jwtDecode(token);
    return decoded.nickname;
  } catch (error) {
    console.error('토큰 디코딩 실패:', error);
    return null;
  }
};

const EditPostPage = ({ params }: { params: { id: string } }) => {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [postNickname, setPostNickname] = useState('');
  const router = useRouter();
  const { isLoggedIn } = useAuthStore();

  // // 로그인한 사용자의 닉네임 가져오기
  // const loggedInNickname = extractNicknameFromToken();
    // 로그인한 사용자의 닉네임 (Zustand 또는 localStorage에서 가져오기)
    const loggedInNickname = localStorage.getItem('nickname');

  useEffect(() => {
    const fetchPost = async () => {
      const post = await getPostById(Number(params.id));
      setTitle(post.title);
      setContent(post.content);
      setPostNickname(post.nickname);  // 게시글 작성자의 닉네임 저장
    };
    fetchPost();
  }, [params.id]);

  const handleSubmit = async () => {
    if (!isLoggedIn) {
      alert('로그인 후 게시글을 수정할 수 있습니다.');
      router.push('/login');
      return;
    }

    // 작성자 검증 (로그인한 닉네임과 게시글 작성자의 닉네임 비교)
    if (loggedInNickname?.toLowerCase() !== postNickname.toLowerCase()) {
      alert('작성자만 게시글을 수정할 수 있습니다.');
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
