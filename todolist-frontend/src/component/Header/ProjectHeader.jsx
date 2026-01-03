import React, { useState, useEffect } from "react";
import ShareModal from "../Modal/ShareModal";
import InviteModal from "../Modal/InviteModal";
import ShareSettingsModal from "../Modal/ShareSettingsModal";
import { Share2, LayoutList, MessageSquare } from "lucide-react";
import { useParams } from "react-router-dom";
import { https_taskflow } from "../../service/api";
import {useProjectContext} from "../../context/ProjectContext";

export default function ProjectHeader() {

  const { members, setMembers } = useProjectContext();

  const { projectId } = useParams();

  const [openShare, setOpenShare] = useState(false);
  const [openInvite, setOpenInvite] = useState(false);
  const [openSettings, setOpenSettings] = useState(false);

  const [selectedEmail, setSelectedEmail] = useState("");
  const [selectedId, setSelectedId] = useState("");

  // -----------------------------------
  // 1. FETCH MEMBERS
  // -----------------------------------
  useEffect(() => {
    const fetchMembers = async () => {
      try {
        const res = await https_taskflow.get(
          `/v1/projects/${projectId}/members`
        );

        setMembers(res.data?.data || []);
      } catch (error) {
        console.error("Lỗi load members:", error);
      }
    };

    if (projectId) fetchMembers();
  }, [projectId]);

  // -----------------------------------
  // 2. CHECK: có thành viên role MEMBER ?
  // -----------------------------------
  const hasMemberRole = members.some((m) => m.role === "MEMBER");

  // -----------------------------------
  // 3. CLICK SHARE
  // -----------------------------------
  const handleClickShare = () => {
    if (hasMemberRole) {
      // Đã có người role MEMBER → mở settings
      setOpenSettings(true);
      setOpenShare(false);
      setOpenInvite(false);
    } else {
      // Chưa có member role = MEMBER → mở ShareModal
      setOpenShare(true);
      setOpenSettings(false);
      setOpenInvite(false);
    }
  };

  return (
    <>
      <header className="flex justify-between items-center px-6 py-3 border-b">
        <div className="flex items-center gap-2 text-sm text-gray-600">
          <span className="cursor-pointer hover:underline">My Projects</span>
          <span>/</span>
        </div>

        <div className="flex items-center gap-5 text-gray-700">
          <button
            onClick={handleClickShare}
            className="flex items-center gap-1 hover:text-black"
          >
            <Share2 size={16} />
            <span>Share</span>
          </button>

          {/* <button
            className="flex items-center gap-1 hover:text-black"
            onClick={() => setOpenSettings((prev) => !prev)}
          >
            <LayoutList size={16} />
            <span>Display</span>
          </button> */}

          {/* <button className="flex items-center gap-1 hover:text-black">
            <MessageSquare size={16} />
            <span>1</span>
          </button>

          <button className="hover:text-black">⋯</button> */}
        </div>
      </header>

      {/* SHARE MODAL */}
      {openShare && (
        <ShareModal
          onClose={() => setOpenShare(false)}
          onSelectUser={(user) => {
            setSelectedEmail(user.email);
            setSelectedId(user.id);
            setOpenShare(false);
            setOpenInvite(true);
          }}
        />
      )}

      {/* INVITE MODAL */}
      {openInvite && (
        <InviteModal
          user={{ email: selectedEmail, id: selectedId }}
          onClose={() => setOpenInvite(false)}
          onInvite={() => {
            setOpenInvite(false);
            setOpenSettings(true);
          }}
        />
      )}

      {/* SHARE SETTINGS MODAL — mở nếu có member role MEMBER */}
      {openSettings && (
        <ShareSettingsModal
          onClose={() => setOpenSettings(false)}
          invitedEmail={selectedEmail}
          members={members}
        />
      )}
    </>
  );
}
