import React from "react";
import { Share2, LayoutList, MessageSquare } from "lucide-react";
import { useParams } from "react-router-dom";
export default function ProjectHeader() {
  return (
    <header className="flex justify-between items-center px-6 py-3 border-b">
      <div className="flex items-center gap-2 text-sm text-gray-600">
        <span className="cursor-pointer hover:underline">My Projects</span>
        <span>/</span>
      </div>

      <div className="flex items-center gap-5 text-gray-700">
        <button className="flex items-center gap-1 hover:text-black">
          <Share2 size={16} />
          <span>Share</span>
        </button>
        <button className="flex items-center gap-1 hover:text-black">
          <LayoutList size={16} />
          <span>Display</span>
        </button>
        <button className="flex items-center gap-1 hover:text-black">
          <MessageSquare size={16} />
          <span>1</span>
        </button>
        <button className="hover:text-black">⋯</button>
      </div>
    </header>
  );
}
