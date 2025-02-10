"use client";

import { useEffect, useState } from 'react';
import { getAllPosts, deletePost } from '../../lib/board';
import Header from '../../components/Header';

interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: string;
}

const BoardListPage = () => {
  const [posts, setPosts] = useState<Post[]>([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

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

  const handleDelete = async (id: number) => {
    try {
      await deletePost(id);
      fetchPosts(currentPage); // 삭제 후 목록 갱신
    } catch (error) {
      console.error('게시글 삭제 실패:', error);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 1) setCurrentPage(currentPage - 1);
  };

  const handleNextPage = () => {
    if (currentPage < totalPages) setCurrentPage(currentPage + 1);
  };

  return (
    <div className="min-h-screen bg-gray-100">
      <Header />
      <main className="max-w-xl mx-auto mt-8">
        <h1 className="text-2xl font-bold mb-4">게시판</h1>

        {posts.map((post) => (
          <div key={post.id} className="mb-6 border rounded-lg overflow-hidden shadow-md">
            <div className="bg-gray-300 flex justify-between items-center p-2">
              <span className="font-bold">{post.title}</span>
              <span className="text-sm text-gray-600">{new Date(post.createdAt).toLocaleDateString()}</span>
            </div>
            <div className="bg-gray-700 text-white p-4">
              <p>{post.content}</p>
            </div>
            <div className="flex justify-end p-2 bg-gray-200">
              <button
                onClick={() => handleDelete(post.id)}
                className="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600"
              >
                삭제
              </button>
            </div>
          </div>
        ))}

        <div className="flex justify-center items-center space-x-4 mt-4">
          <button
            onClick={handlePrevPage}
            disabled={currentPage === 1}
            className={`px-4 py-2 rounded ${currentPage === 1 ? 'bg-gray-300' : 'bg-gray-500 text-white'}`}
          >
            &lt;
          </button>
          <span>{currentPage}</span>
          <button
            onClick={handleNextPage}
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
