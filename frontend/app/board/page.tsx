"use client";

import { useEffect, useState } from 'react';
import { getAllPosts } from '../../lib/board';
import { useRouter } from 'next/navigation';

interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: string;
  user: {   
    id: number;
    email: string;
    nickname?: string;  //(optional 처리)
  };
}

const BoardListPage = () => {
  const [posts, setPosts] = useState<Post[]>([]);  
  const [currentPage, setCurrentPage] = useState(1);  
  const [totalPages, setTotalPages] = useState(1);  
  const router = useRouter();  

  useEffect(() => {
    fetchPosts(currentPage);
  }, [currentPage]);

  const fetchPosts = async (page: number) => {
    try {
      const data = await getAllPosts(page);
      setPosts(data.boards);
      setTotalPages(data.totalPages);
    } catch (error) {
      console.error('게시글 불러오기 실패:', error);
    }
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <main className="max-w-4xl mx-auto mt-8 mb-16">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-2xl font-bold">게시판</h1>

          <button
            onClick={() => router.push('/board/write')}  
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600"
          >
            글쓰기
          </button>
        </div>

        {posts.map((post) => (
          <div
            key={post.id}
            className="mb-6 border rounded-lg overflow-hidden shadow-md cursor-pointer hover:shadow-lg transition"
            onClick={() => router.push(`/board/${post.id}`)}  
          >
            <div className="bg-gray-300 p-1">
              <div className="flex justify-between items-center">
                <span className="font-bold text-base pl-5">{post.title}</span>

                <div className="text-right">
                  <div className="text-sm text-gray-700 text-right mt-1">작성자: {post.user.nickname || "익명"}
                  </div>
                  <span className="text-sm text-gray-600">{new Date(post.createdAt).toLocaleDateString()}</span>
                </div>
              </div>
            </div>

            <div className="bg-white text-black p-6 h-39">
              <p>{post.content.replace(/[#_*`>~\\-]/g, '').substring(0, 100)}...</p>
            </div>
          </div>
        ))}

        <div className="max-w-4xl mx-auto flex justify-center items-center space-x-4">
          <button
            onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 1))}
            disabled={currentPage === 1}
            className={`px-4 py-2 rounded ${currentPage === 1 ? 'bg-gray-300' : 'bg-gray-500 text-white'}`}
          >
            &lt;
          </button>
          <span>{currentPage}</span>  
          <button
            onClick={() => setCurrentPage((prev) => Math.min(prev + 1, totalPages))}
            disabled={currentPage === totalPages}
            className={`px-4 py-2 rounded ${currentPage === totalPages ? 'bg-gray-300' : 'bg-gray-500 text-white'}`}
          >
            &gt;
          </button>
        </div>
      </main>
    </div>
  );
};

export default BoardListPage;
