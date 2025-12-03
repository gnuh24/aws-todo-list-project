import { useState, useRef, useEffect } from "react";
import { ChevronDown, Copy, Lock, User, X } from "lucide-react";

export default function ShareSettings({ onClose }) {
  const [email, setEmail] = useState("");
  const [openDropdown, setOpenDropdown] = useState(null); // index dropdown đang mở

  const [collaborators, setCollaborators] = useState([
    {
      name: "Duongbinhminh10032004 (Me)",
      email: "duongbinhminh10032004@gmail.com",
      role: "Owner",
      status: "active",
    },
    {
      name: "trungkien1862@gmail.com",
      email: "trungkien1862@gmail.com",
      role: "Collaborator",
      status: "pending",
    },
  ]);

  const dropdownRef = useRef();

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

  const handleInvite = () => {
    if (!email.trim()) return;

    setCollaborators([
      ...collaborators,
      {
        name: email.trim(),
        email: email.trim(),
        role: "Collaborator",
        status: "pending",
      },
    ]);

    setEmail("");
  };

  const removeCollaborator = (emailToRemove) => {
    setCollaborators(collaborators.filter((c) => c.email !== emailToRemove));
    setOpenDropdown(null);
  };

  return (
    <div
      className=" fixed top-16 right-6 
    w-[480px] 
    bg-white rounded-xl shadow-2xl 
    overflow-y-auto z-50 
    animate-slideIn"
    >
      <div className=" bg-white rounded-2xl shadow-xl p-6 animate-fadeIn relative">
        {/* Close */}
        <button
          className="absolute top-4 right-4 text-gray-600 hover:text-gray-800"
          onClick={onClose}
        >
          <X size={20} />
        </button>

        {/* Input */}
        <input
          type="text"
          placeholder="Add people by name or email"
          className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-gray-300"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleInvite()}
        />

        {/* Access section */}
        <div className="mt-6">
          <div className="text-xs font-semibold text-gray-700 mb-2">Access</div>

          <div className="flex items-center justify-between p-3 border rounded-xl hover:bg-gray-50 cursor-pointer">
            <div className="flex items-center gap-3">
              <Lock size={18} />
              <div>
                <div className="text-sm font-medium">Restricted</div>
                <div className="text-xs text-gray-500">
                  Only invited people can edit
                </div>
              </div>
            </div>

            <ChevronDown size={18} />
          </div>
        </div>

        {/* In this project */}
        <div className="mt-6">
          <div className="text-xs font-semibold text-gray-700 mb-2">
            In this project
          </div>

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
                <span className="text-xs text-gray-500 cursor-pointer hover:underline">
                  Leave
                </span>
              ) : (
                <div ref={dropdownRef} className="relative">
                  <button
                    className="flex items-center gap-1 text-sm text-gray-700 hover:text-black"
                    onClick={() =>
                      setOpenDropdown(openDropdown === i ? null : i)
                    }
                  >
                    {c.role}
                    <ChevronDown size={16} />
                  </button>

                  {/* Dropdown */}
                  {openDropdown === i && (
                    <div className="absolute right-0 mt-2 w-48 bg-white border rounded-xl shadow-lg py-2 z-50">
                      <button
                        className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50"
                        onClick={() => removeCollaborator(c.email)}
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

      {/* Animation */}
      <style>{`
        @keyframes fadeIn {
          from { opacity: 0; transform: scale(0.96); }
          to { opacity: 1; transform: scale(1); }
        }
        .animate-fadeIn {
          animation: fadeIn 0.25s ease-out;
        }
      `}</style>
    </div>
  );
}
