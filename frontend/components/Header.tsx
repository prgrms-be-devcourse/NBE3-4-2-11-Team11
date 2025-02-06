"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";

const Header = () => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const router = useRouter();

  useEffect(() => {
    //  JWT 토큰 확인하여 로그인 상태 업데이트
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token); //  토큰이 존재하면 true 아니면 false
  }, []);

  const handleLogout = () => {
    localStorage.removeItem("access_token"); //  토큰 삭제
    setIsLoggedIn(false); // 상태 업데이트
    router.push("/");  //  메인화면 이동
  };

  return (
    <header className="bg-gray-900 text-white py-4 px-8 flex justify-between items-center">
      <div className="text-xl font-bold">
      <Link href="/" className="hover:text-gray-400">POFO</Link>
        {/*  POFO 로고 클릭시 메인페이지로 */}
      </div>
      <nav>
        <ul className="flex space-x-6">
          <li>
            <Link href="/notice" className="hover:text-gray-400">공지사항</Link>
          </li>
          <li className="relative group">
            <Link href="/mypage" className="hover:text-gray-400">마이페이지</Link>
            <ul className="absolute left-0 mt-2 w-32 bg-gray-800 text-white opacity-0 group-hover:opacity-100 transition-opacity">
              <li className="px-4 py-2 hover:bg-gray-700">
                <Link href="/mypage/resume">이력서</Link>
              </li>
              <li className="px-4 py-2 hover:bg-gray-700">
                <Link href="/mypage/projects">프로젝트</Link>
              </li>
              <li className="px-4 py-2 hover:bg-gray-700">
                <Link href="/mypage/board">게시판</Link>
              </li>
            </ul>
          </li>
          <li>
            <Link href="/contact" className="hover:text-gray-400">문의하기</Link>
          </li>
          <li>
            {/*<Link href="/login" className="hover:text-gray-400">로그인</Link>*/}
            {isLoggedIn ? (
                //  로그인 상태면 로그아웃 버튼 표시
                <button onClick={handleLogout} className="hover:text-red-400">로그아웃</button>
            ) : (
                //  로그아웃 상태면 로그인 버튼 표시
                <Link href="/login" className="hover:text-gray-400">로그인</Link>
            )}
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
