import { useState, useRef, useEffect } from "react";
import { ChevronDown, Copy, Lock, User, X } from "lucide-react";
import { https_taskflow } from "../../service/api";
import { useParams } from "react-router-dom";
import { toast } from "sonner";


export default function ShareSettings({ onClose }) {
  const [email, setEmail] = useState("");
  const [openDropdown, setOpenDropdown] = useState(null);
const { projectId } = useParams();
  const [collaborators, setCollaborators] = useState([]);

  const dropdownRef = useRef();

  // Load members từ API
  useEffect(() => {
    const fetchMembers = async () => {
      try {
        const res = await https_taskflow.get(
          `/v1/projects/${projectId}/members`
        );

        const members = res.data?.data || [];

        const formatted = members.map((m) => ({
          id: m.id,
          accountId: m.accountId,
          name: m.displayName,
          email: m.email || "unknown@mail.com", // Nếu backend không trả email
          avatar: m.avatar,
          role: m.role === "OWNER" ? "Owner" : "Member",
          status: m.status === "PENDING" ? "pending" : "active",
        }));

        setCollaborators(formatted);
      } catch (err) {
        console.error("Failed to load members:", err);
      }
    };

    fetchMembers();
  }, [projectId]);

  // Đóng dropdown khi click ra ngoài
  useEffect(() => {
    const handleClick = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpenDropdown(null);
      }
    };
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  const removeCollaborator = async (memberId) => {
    try {
      await https_taskflow.delete(
        `/v1/projects/${projectId}/members/${memberId}`
      );

      setCollaborators(collaborators.filter((c) => c.id !== memberId));
      toast.success("Xóa member thành công" )
      setOpenDropdown(null);
    } catch (err) {
      console.error("Failed to remove collaborator:", err);
    }
  };

  return (
    <div className="fixed top-16 right-6 w-[480px] bg-white rounded-xl shadow-2xl overflow-y-auto z-50 animate-slideIn">
      <div className="bg-white rounded-2xl shadow-xl p-6 animate-fadeIn relative">
        {/* Close */}
        <button className="absolute top-4 right-4 text-gray-600 hover:text-gray-800" onClick={onClose}>
          <X size={20} />
        </button>

        {/* Input */}
        {/* <input
          type="text"
          placeholder="Add people by name or email"
          className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-gray-300"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        /> */}

        {/* Access section */}
        <div className="mt-6">
          <div className="text-xs font-semibold text-gray-700 mb-2">Access</div>

          <div className="flex items-center justify-between p-3 border rounded-xl hover:bg-gray-50 cursor-pointer">
            <div className="flex items-center gap-3">
              <Lock size={18} />
              <div>
                <div className="text-sm font-medium">Restricted</div>
                <div className="text-xs text-gray-500">Only invited people can edit</div>
              </div>
            </div>
            <ChevronDown size={18} />
          </div>
        </div>

        {/* In this project */}
        <div className="mt-6">
          <div className="text-xs font-semibold text-gray-700 mb-2">In this project</div>

          {collaborators.map((c, i) => (
            <div
              key={i}
              className="flex items-center justify-between py-3 border-b last:border-none relative"
            >
              {/* Left info */}
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center text-sm">
                  <User size={16} />
                </div>

                <div>
                  <div className="text-sm font-medium">{c.name}</div>
                  <div className="text-xs text-gray-500">{c.email}</div>
                  {c.status === "pending" && (
                    <div className="text-xs text-gray-400">Pending</div>
                  )}
                </div>
              </div>

              {/* Right side */}
              {c.role === "Owner" ? (
                <span className="text-xs text-gray-500">Owner</span>
              ) : (
                <div ref={dropdownRef} className="relative">
                  <button
                    className="flex items-center gap-1 text-sm text-gray-700 hover:text-black"
                    onClick={() => setOpenDropdown(openDropdown === i ? null : i)}
                  >
                    {c.role}
                    <ChevronDown size={16} />
                  </button>

                  {/* Dropdown */}
                  {openDropdown === i && (
                    <div className="absolute right-0 mt-2 w-48 bg-white border rounded-xl shadow-lg py-2 z-50">
                      <button
                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                        onClick={() => removeCollaborator(c.id)}
                      >
                        Remove from project
                      </button>
                    </div>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>

        {/* Bottom */}
        <div className="flex justify-between items-center mt-6 pt-4 border-t">
          <button className="flex items-center gap-2 text-sm text-gray-600 hover:underline">
            ❓ Learn about sharing
          </button>

          <button className="flex items-center gap-2 text-sm border px-3 py-2 rounded-md hover:bg-gray-100">
            <Copy size={16} /> Copy link
          </button>
        </div>
      </div>
    </div>
  );
}
