import Link from "next/link";

const Header = () => {
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
            </ul>
          </li>
          <li>
            <Link href="/contact" className="hover:text-gray-400">문의하기</Link>
          </li>
          <li>
            <Link href="/login" className="hover:text-gray-400">로그인</Link>
          </li>
        </ul>
      </nav>
    </header>
  );
};

export default Header;
