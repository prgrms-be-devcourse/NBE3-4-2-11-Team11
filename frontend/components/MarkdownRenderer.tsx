import React from "react";
import ReactMarkdown from "react-markdown";
import remarkGfm from "remark-gfm";
import remarkBreaks from "remark-breaks"; // 줄바꿈 지원
import rehypeRaw from "rehype-raw"; 
import rehypeSanitize from "rehype-sanitize";
import rehypeHighlight from "rehype-highlight";
import "highlight.js/styles/github.css"; // 코드 하이라이트 스타일 추가

const MarkdownRenderer = ({ content }: { content: string }) => {
  return (
    <div className="prose max-w-none">
      <ReactMarkdown 
        remarkPlugins={[remarkGfm, remarkBreaks]} 
        rehypePlugins={[rehypeRaw, rehypeSanitize, rehypeHighlight]} //rehype-highlight 추가
      >
        {content}
      </ReactMarkdown>
    </div>
  );
};

export default MarkdownRenderer;
