"use client";

import { useRouter } from 'next/navigation';
import { getPostById, deletePost } from '../../../lib/board';
import { useEffect, useState } from 'react';
import Header from '../../../components/Header';
import MarkdownRenderer from '../../../components/MarkdownRenderer';
import { useAuthStore } from '@/store/authStore';  // 로그인 상태 가져오기

interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: string;
}

const PostDetailPage = ({ params }: { params: { id: string } }) => {
  const [post, setPost] = useState<Post | null>(null);
  const router = useRouter();
  const { isLoggedIn } = useAuthStore();  // 로그인 상태 확인

  useEffect(() => {
    const fetchPost = async () => {
      const data = await getPostById(Number(params.id));
      setPost(data);
    };
    fetchPost();
  }, [params.id]);

  const handleDelete = async () => {
    if (!isLoggedIn) {
      alert('로그인 후 게시글을 삭제할 수 있습니다.');
      router.push('/login');
      return;
    }

    if (confirm('정말 이 게시글을 삭제하시겠습니까?')) {
      await deletePost(Number(params.id));
      router.push('/board');  // 삭제 후 목록 페이지로 리디렉트
    }
  };

  if (!post) return <p>로딩 중...</p>;

  return (
    <div className="min-h-screen bg-gray-100">
      <Header />
      <main className="max-w-xl mx-auto mt-8">
        <h1 className="text-2xl font-bold mb-4">{post.title}</h1>
        <p className="text-gray-600 mb-4">{new Date(post.createdAt).toLocaleDateString()}</p>
        <div className="bg-gray-700 text-white p-4 mb-4 rounded">
          <MarkdownRenderer content={post.content} />
        </div>
        <div className="flex justify-end space-x-4">
          <button
            onClick={() => router.push(`/board/${params.id}/edit`)}
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
