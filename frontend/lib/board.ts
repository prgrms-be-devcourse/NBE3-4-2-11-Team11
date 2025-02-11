import apiClient from './fetch';
import { RsData } from '../utils/types';

/** 게시글 타입 정의 */
interface Post {
  id: number;
  title: string;
  content: string;
  createdAt: string;  // 작성일자 필드 추가
}

interface PostList {
  currentPage: number;
  totalPages: number;
  totalItems: number;
  boards: Post[];
}


// 게시글 목록 조회
export const getAllPosts = async (page: number = 1, size: number = 10): Promise<PostList> => {
  const response = await apiClient.get<RsData<PostList>>('/user/boards', { params: { page, size } });
  return response.data.data;
};


//게시글 상세 조회 (특정 게시글 ID로 조회)
export const getPostById = async (id: number): Promise<Post> => {
  const response = await apiClient.get<RsData<Post>>(`/user/boards/${id}`);
  return response.data.data;
};


//게시글 작성 (로그인한 사용자의 userId로 작성, 보통 토큰으로 처리)
export const createPost = async (postData: { title: string; content: string }): Promise<Post> => {
  const response = await apiClient.post<RsData<Post>>('/user/boards', postData);
  return response.data.data;
};


//게시글 수정 (특정 게시글 ID로 수정)
export const updatePost = async (id: number, postData: { title: string; content: string }): Promise<Post> => {
  const response = await apiClient.patch<RsData<Post>>(`/user/boards/${id}`, postData);
  return response.data.data;
};


//게시글 삭제 (특정 게시글 ID로 삭제)
export const deletePost = async (id: number): Promise<{ message: string }> => {
  const response = await apiClient.delete<RsData<{ message: string }>>(`/user/boards/${id}`);
  return response.data.data;
};
