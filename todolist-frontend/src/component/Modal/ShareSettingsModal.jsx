import { useState, useRef, useEffect } from "react";
import { ChevronDown, Copy, Lock, User, X } from "lucide-react";
import { https_taskflow, https_user } from "../../service/api";
import { useParams } from "react-router-dom";
import { toast } from "sonner";

export default function ShareSettings({ onClose }) {
  const { projectId } = useParams();

  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);
  const [userFound, setUserFound] = useState(null);

  const [openDropdown, setOpenDropdown] = useState(null);
  const [collaborators, setCollaborators] = useState([]);

  const dropdownRef = useRef();

  // -----------------------------------
  // FETCH MEMBERS
  // -----------------------------------
  useEffect(() => {
    const fetchMembers = async () => {
      try {
        const res = await https_taskflow.get(`/v1/projects/${projectId}/members`);
        const members = res.data?.data || [];

        const formatted = members.map((m) => ({
          id: m.id,
          accountId: m.accountId,
          name: m.displayName,
          email: m.email || "unknown@mail.com",
          avatar: m.avatar,
          role: m.role === "OWNER" ? "Owner" : "Member",
          status: m.status === "PENDING" ? "pending" : "active",
        }));

        setCollaborators(formatted);
      } catch (err) {
        console.error("FAILED MEMBER LIST:", err);
      }
    };

    fetchMembers();
  }, [projectId]);
const handleCopyLink = () => {
  const url = window.location.href; // hoặc custom link nếu bạn muốn
  navigator.clipboard.writeText(url);

  toast.success("Copied project link!");
};


  // -----------------------------------
  // SEARCH USER WHEN TYPING EMAIL
  // -----------------------------------
  useEffect(() => {
    if (!email) {
      setUserFound(null);
      return;
    }

    const delay = setTimeout(async () => {
      try {
        setLoading(true);
        const res = await https_user.get(`/v1/accounts?email=${email}`);
console.log(res.data.data.id)
        if (res.data?.data) {
          setUserFound(res.data.data);
          console.log(userFound)
        } else {
          setUserFound(null);
        }
      } catch (err) {
        console.error("Error searching user:", err);
        setUserFound(null);
      } finally {
        setLoading(false);
      }
    }, 500);

    return () => clearTimeout(delay);
  }, [email]);


  // -----------------------------------
  // INVITE USER
  // -----------------------------------
  const handleInviteUser = async () => {
    if (!userFound) return;

    try {
      const body = {
        idAccount: userFound.id,
        role: "MEMBER"

      };

      await https_taskflow.post(
        `/v1/projects/${projectId}/members`,
        body
      );

      toast.success("User invited successfully!");

      setEmail("");
      setUserFound(null);

      // Refresh members list
      setCollaborators((prev) => [
        ...prev,
        {
          id: crypto.randomUUID(),
          accountId: userFound.id,
          name: userFound.displayName,
          email: userFound.email,
          avatar: userFound.avatar,
          role: "Member",
          status: "pending",
        },
      ]);
    } catch (err) {
      console.error("ERROR INVITING USER:", err);
      toast.error("Thành viên đã có trong dự án");
    }
  };


  // -----------------------------------
  // REMOVE MEMBER
  // -----------------------------------
  const removeCollaborator = async (memberId) => {
    try {
      await https_taskflow.delete(`/v1/projects/${projectId}/members/${memberId}`);

      setCollaborators(collaborators.filter((c) => c.id !== memberId));
      toast.success("Member removed");
      setOpenDropdown(null);
    } catch (err) {
      console.error(err);
    }
  };


  return (
    <div className="fixed top-16 right-6 w-[480px] bg-white rounded-xl shadow-2xl overflow-y-auto z-50 animate-slideIn">
      <div className="bg-white rounded-2xl shadow-xl p-6 animate-fadeIn relative">
        
        {/* Close */}
        <button className="absolute top-4 right-4 text-gray-600 hover:text-gray-800" 
          onClick={onClose}>
          <X size={20} />
        </button>

        {/* Input search */}
        <input
          type="text"
          placeholder="Add people by name or email"
          className="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-gray-300"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />

        {/* Loading */}
        {loading && (
          <div className="mt-3 text-sm text-gray-500">Searching...</div>
        )}

        {/* Found user */}
        {userFound && !loading && (
          <div
            className="mt-3 p-3 bg-gray-50 rounded-xl border cursor-pointer hover:bg-gray-100"
            onClick={handleInviteUser}
          >
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 bg-gray-200 rounded-full"></div>
              <div>
                <div className="text-sm font-semibold">{userFound.displayName}</div>
                <div className="text-xs text-gray-500">{userFound.email}</div>
              </div>
            </div>
          </div>
        )}

        {/* No user found */}
        {!loading && email && !userFound && (
          <div className="mt-3 p-3 text-sm text-gray-600 border rounded-xl">
            No user found
          </div>
        )}

        {/* Access */}
        <div className="mt-6">
          <div className="text-xs font-semibold text-gray-700 mb-2">Access</div>
          <div className="flex items-center justify-between p-3 border rounded-xl">
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

        {/* Members */}
        <div className="mt-6">
          <div className="text-xs font-semibold text-gray-700 mb-2">In this project</div>

          {collaborators.map((c, i) => (
            <div key={i} className="flex items-center justify-between py-3 border-b last:border-none">
              
              <div className="flex items-center gap-3">
                <div className="w-9 h-9 rounded-full bg-gray-200 flex items-center justify-center">
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

                  {openDropdown === i && (
                    <div className="absolute right-0 mt-2 w-48 bg-white border rounded-xl shadow-lg py-2">
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

        {/* Footer */}
        <div className="flex justify-between items-center mt-6 pt-4 border-t">
          <button className="text-sm text-gray-600">❓ Learn about sharing</button>
          <button onClick={handleCopyLink} className="flex items-center gap-2 text-sm border px-3 py-2 rounded-md">
            <Copy size={16} /> Copy link
          </button>
        </div>
      </div>
    </div>
  );
}
