import {
  ArchiveRestore,
  Copy,
  CopyPlus,
  Edit2,
  MoveRight,
  Trash,
} from "lucide-react";
import { useState, useRef, useEffect } from "react";
import { toast } from "sonner";

export default function MoreMenu({ onEdit, onDelete, onArchive }) {
  const [open, setOpen] = useState(false);
  const menuRef = useRef(null);

  // Đóng menu khi click ra ngoài
  useEffect(() => {
    function handleClickOutside(e) {
      if (menuRef.current && !menuRef.current.contains(e.target)) {
        setOpen(false);
      }
    }
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="relative inline-block" ref={menuRef}>
      {/* BUTTON */}
      <button
        onClick={() => setOpen(!open)}
        className="hover:text-gray-600 text-gray-400 px-2 text-xl select-none"
      >
        ⋯
      </button>

      {/* DROPDOWN */}
      {open && (
        <div className="absolute right-0 mt-2 w-48 bg-white shadow-xl rounded-xl border border-gray-100 z-50 py-2 animate-fade">
          <button
            onClick={onEdit}
            className="flex items-center w-full px-4 py-2 text-gray-700 hover:bg-gray-50"
          >
            <Edit2 size={14} /> <span className="ml-2">Edit</span>
          </button>

          <button
            onClick={() => toast.error("Move to...")}
            className="flex items-center w-full px-4 py-2 text-gray-700 hover:bg-gray-50"
          >
            <MoveRight size={14} /> <span className="ml-2">Move to…</span>
          </button>

          <button
            onClick={() => toast.error("Duplicate")}
            className="flex items-center w-full px-4 py-2 text-gray-700 hover:bg-gray-50"
          >
            <CopyPlus size={14} />
            <span className="ml-2">Duplicate</span>
          </button>

          <button
            onClick={() => navigator.clipboard.writeText("link-to-section")}
            className="flex items-center w-full px-4 py-2 text-gray-700 hover:bg-gray-50"
          >
            <Copy size={14} />
            <span className="ml-2">Copy link to section</span>
          </button>

          <div className="border-t my-2"></div>

          <button
            onClick={onArchive}
            className="flex items-center w-full px-4 py-2 text-gray-700 hover:bg-gray-50"
          >
            <ArchiveRestore size={14} />
            <span className="ml-2">Archive</span>
          </button>

          <button
            onClick={onDelete}
            className="flex items-center w-full px-4 py-2 text-red-500 hover:bg-red-50"
          >
            <Trash size={14} />
            <span className="ml-2">Delete</span>
          </button>
        </div>
      )}
    </div>
  );
}
