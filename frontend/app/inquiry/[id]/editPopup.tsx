"use client";

import React from "react";
import styles from "./editPopup.module.css";

type EditPopupProps = {
  content: string;
  onClose: () => void;
  onSubmit: (newContent: string) => void;
  onContentChange: (newContent: string) => void;
};

const EditPopup: React.FC<EditPopupProps> = ({
  content,
  onClose,
  onSubmit,
  onContentChange,
}) => {
  const [editedContent, setEditedContent] = React.useState(content);

  const handleSubmit = () => {
    onSubmit(editedContent);
  };

  return (
    <div className={styles.popup}>
      <div className={styles.popupContent}>
        <textarea
          value={editedContent}
          onChange={(e) => {
            setEditedContent(e.target.value);
            onContentChange(e.target.value);
          }}
        />
        <button onClick={handleSubmit}>저장</button>
        <button onClick={onClose}>닫기</button>
      </div>
    </div>
  );
};

export default EditPopup;