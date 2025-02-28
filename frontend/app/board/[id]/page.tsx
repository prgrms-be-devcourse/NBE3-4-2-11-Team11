"use client";

import { useRouter, useParams } from 'next/navigation';  // useParams 추가
import { getPostById, deletePost } from '../../../lib/board';
import { useEffect, useState } from 'react';
import MarkdownRenderer from '../../../components/MarkdownRenderer';
import { useAuthStore } from '@/store/authStore';
import { getAccessToken } from '@/utils/token';
import { decodeJWT } from '@/utils/decodeJWT';
import { Post } from '../../../lib/board'; // Post 타입이 정의된 경로로 수정

// JWT 토큰에서 로그인한 사용자의 이메일을 추출하는 함수
const getLoggedInEmail = (): string | null => {
  const token = getAccessToken();
  if (!token) return null;

  const decoded: any = decodeJWT(token);
  return decoded?.sub || null;  // JWT의 sub 필드에 이메일이 저장됨
};

const PostDetailPage = () => {  // params 제거
  const [post, setPost] = useState<Post | null>(null);
  const router = useRouter();
  const params = useParams();  // useParams 훅으로 params 가져오기
  const { isLoggedIn } = useAuthStore();

  const loggedInEmail = getLoggedInEmail();
  const postId = params.id as string;  // params.id는 문자열로 반환됨

  useEffect(() => {
    const fetchPost = async () => {
      try {
        const data = await getPostById(Number(postId));  // postId 사용
        setPost(data);
      } catch (error) {
        console.error('게시글 불러오기 실패:', error);
      }
    };
    fetchPost();
  }, [postId]);

  const handleDelete = async () => {
    if (!isLoggedIn) {
      alert('로그인 후 게시글을 삭제할 수 있습니다.');
      router.push('/login');
      return;
    }

    if (!post || !loggedInEmail || loggedInEmail.toLowerCase() !== post.email.toLowerCase()) {
      alert('작성자만 게시글을 삭제할 수 있습니다.');
      return;
    }

    if (confirm('정말 이 게시글을 삭제하시겠습니까?')) {
      try {
        await deletePost(Number(postId));  // postId 사용
        alert('게시글이 성공적으로 삭제되었습니다.');
        router.push('/board');  // 삭제 후 목록 페이지로 이동
      } catch (error) {
        console.error('게시글 삭제 실패:', error);
        alert('게시글 삭제 중 오류가 발생했습니다.');
      }
    }
  };

  if (!post) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-100">
        <p className="text-lg text-gray-600">게시글을 불러오는 중...</p>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-100 p-8 flex justify-center">
      <main className="w-full max-w-4xl bg-white p-6 rounded shadow-md">
        <div className="flex items-center mb-4">
          <div className="bg-white text-black p-4 rounded border w-3/4">
            <h1 className="text-3xl font-bold">{post.title}</h1>
          </div>
          <div className="text-right w-1/4 ml-4">
            <p className="text-gray-600 text-sm mb-1">작성자: {post.email}</p>
            <p className="text-gray-600 text-sm mt-2">{new Date(post.createdAt).toLocaleDateString()}</p>
          </div>
        </div>

        <div className="bg-white text-black p-6 mb-4 rounded h-[60vh] overflow-y-auto border">
          <MarkdownRenderer content={post.content} />
        </div>

        <div className="flex justify-end space-x-4">
          <button
            onClick={() => router.push(`/board/${postId}/edit`)}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            수정
          </button>
          <button
            onClick={handleDelete}
            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
          >
            삭제
          </button>
        </div>
      </main>
    </div>
  );
};




export default PostDetailPage;
