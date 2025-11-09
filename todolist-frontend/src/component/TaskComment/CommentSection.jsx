import { useState, useEffect, useRef } from "react";
import {  Input, Button } from "antd";
import {
    CheckCircleFilled,
    LockOutlined,
    PaperClipOutlined,
    AudioOutlined,
    SmileOutlined,
    EnterOutlined,
    DownOutlined,
    UpOutlined
} from "@ant-design/icons";
import dayjs from "dayjs";
import {https_taskflow} from "../../service/api";

export default function CommentSection({ isOpenComment, comments, handleComment, onUpdateComment, onDeleteComment }) {
    const [newComment, setNewComment] = useState("");
    const [showComments, setShowComments] = useState(isOpenComment ?? false);
    const [showOption, setShowOption] = useState(null);
    const [showEditForm, setShowEditForm] = useState(null);
    const [isExpanded, setIsExpanded] = useState(false);
    const [openMenu, setOpenMenu] = useState(null);
    const [auth, setAuth] = useState(() => {
        const raw = localStorage.getItem("USER_INFO");
        if (!raw) return null;
        try { return JSON.parse(raw); } catch { return null; }
    });
    const formatToDisplay = "HH:mm DD/MM"
    const containerRef = useRef(null);
    const menuRef = useRef(null);

    useEffect(() => {
        if (showComments && containerRef.current) {
            containerRef.current.scrollTop = containerRef.current.scrollHeight;
        }
    }, [showComments, comments.length]); // scroll khi mở hoặc có comment mới

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (menuRef.current && !menuRef.current.contains(event.target)) {
                setOpenMenu(null); // đóng menu
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    useEffect(() => {
        setShowComments(isOpenComment);
    }, [isOpenComment]);


    const handleSubmit = async () => {
        if (!newComment.trim()) return;
        await handleComment(newComment);
        setNewComment("");
    };

    const handleUpdate = async (idComment) => {
        if (!newComment.trim()) return;
        await onUpdateComment(newComment, idComment);
        setNewComment("");
        setShowEditForm(null);
    };

    const handleDelete = async (idComment) => {
        if (!window.confirm("Bạn có muốn xóa comment này không?")) return
        await onDeleteComment(idComment);
    }



    return (
        <div className="w-full border-t pt-10">
            {/* Header */}
            <div className="flex items-center gap-2 mb-3 cursor-pointer select-none" onClick={() => setShowComments(!showComments)}>
                <UpOutlined
                    className={`transition-transform duration-400 ${showComments ? 'rotate-180' : 'rotate-90'}`}
                />
                <span className="font-medium text-gray-700">Comments</span>
                <span className="text-sm text-gray-500">{comments.length}</span>
            </div>

            {/* Comment List */}
            <div
                ref={containerRef}
                className={`space-y-4 mb-4 pr-2 transition-all duration-300 overflow-y-auto ${
                    showComments ? "max-h-80" : "max-h-0"
                }`}
            >
                {comments.map((c, i) => {
                    const isMe = c.accountId === auth.id;
                    return (
                        <div
                            key={i}
                            className={`flex gap-2 py-2 px-2 rounded-lg ${
                                isMe ? 'flex-row-reverse text-right' : 'flex-row text-left'
                            }`}
                            onMouseEnter={() => {
                                if (showEditForm !== c.id) setShowOption(c.id)
                            }}
                            onMouseLeave={() => {
                                setShowOption(null)
                                setOpenMenu(null)
                            }}
                        >
                            {/* Avatar */}
                            <div className="w-9 h-9 rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold">
                                {c.authorAvatar? (
                                    <img
                                        src={c.authorAvatar}
                                        alt="avatar"
                                        className="w-full h-full object-cover"
                                        onError={(e) => (e.currentTarget.src = '/default-avatar.png')}
                                    />
                                ) : (
                                    c?.authorName?.[0]?.toUpperCase() || 'U'
                                )}
                            </div>

                            {/* Comment box */}
                            {showEditForm !== c.id && (<div className={`border rounded-lg px-5 py-4 bg-white shadow-sm max-w-xs ${
                                isMe ? 'bg-green-300 border-green-300' : 'bg-white border-gray-300'
                            } relative`}>
                                <div className="text-sm font-semibold text-gray-600 mb-1">
                                    <div className="text-sm font-semibold text-gray-600 mb-1">
                                        {c.authorName}
                                    </div>
                                    <div className="text-xs text-gray-600 mb-1">
                                        {dayjs(c.updatedAt).format(formatToDisplay)}
                                    </div>

                                </div>

                                <div className="text-sm text-gray-700 whitespace-pre-line pt-3">{c.comment}</div>
                            </div>)}

                            {showEditForm === c.id && (<div className="w-full border rounded-md p-3">
                                <Input.TextArea
                                    rows={3}
                                    defaultValue={c.comment}
                                    onChange={(e) => setNewComment(e.target.value)}
                                    className="border-none focus:ring-0 resize-none"
                                    autoFocus
                                />
                                <div className="flex justify-between items-center mt-2">
                                    <div className="flex gap-3 text-gray-400 text-lg">
                                        {/*<PaperClipOutlined />*/}
                                        {/*<AudioOutlined />*/}
                                        <SmileOutlined />
                                        {/*<EnterOutlined />*/}
                                    </div>
                                    <div className="flex gap-2">
                                        <Button onClick={()=> setShowEditForm(null)}>Cancel</Button>
                                        <Button type="primary" danger onClick={() => handleUpdate(c.id)}>Update
                                            Update
                                        </Button>
                                    </div>
                                </div>
                            </div>)}

                            {/* 3 chấm ngoài comment box */}
                            {isMe && showOption === c.id && (
                                <div className="relative flex items-end" ref={menuRef}>
                                    <button
                                        onClick={() => {
                                            if (openMenu === null) {
                                                setOpenMenu(c.id)
                                            }else{
                                                setOpenMenu(null)
                                            }
                                        }}

                                        className="w-5 h-5 flex items-center justify-center text-gray-500 hover:text-gray-700 bg-gray-100 hover:bg-gray-200 rounded-full transition-colors duration-200 shadow-sm focus:outline-none"
                                    >
                                        ⋮
                                    </button>

                                    {openMenu === c.id && (
                                        <div className="absolute bottom-5 mb-2 right-0 w-24 bg-white border rounded shadow-md z-10">
                                            <button className="w-full text-left px-2 py-1 hover:bg-gray-100 text-sm" onClick={() => {
                                                setShowEditForm(c.id)
                                                setShowOption(null)
                                                setIsExpanded(false)
                                                setNewComment("") // Reset lại biến newComment trong trường hợp đang tạo mới mà cancel
                                            }}>Edit</button>
                                            <button className="w-full text-left px-2 py-1 hover:bg-gray-100 text-sm text-red-500" onClick={
                                                // Đóng form edit và add nếu đang mở
                                                () => {
                                                    setShowEditForm(null)
                                                    setIsExpanded(false)
                                                    setNewComment("")
                                                    handleDelete(c.id)
                                                    setShowOption(null) // Đóng form chọn
                                                }
                                            }>Delete</button>
                                        </div>
                                    )}
                                </div>

                            )}
                        </div>
                    );
                })}
            </div>


            {/* Comment Input */}
            {!isExpanded && (
                <div className="w-full flex items-center gap-2">
                    <div className="w-9 h-9 rounded-full bg-[#56D08A] text-white flex items-center justify-center font-semibold overflow-hidden">
                        {auth.avatar? (
                            <img
                                src={auth.avatar}
                                alt="avatar"
                                className="w-full h-full object-cover"
                                onError={(e) => (e.currentTarget.src = '/default-avatar.png')}
                            />
                        ) : (
                            auth?.displayName?.[0]?.toUpperCase() || 'U'
                        )}
                    </div>
                    <div className="flex-1 flex items-center gap-2 border rounded-full px-3 py-2 hover:bg-gray-100" onClick={() => {
                        setIsExpanded(true)
                        setShowEditForm(null)
                        setNewComment("") // Reset lại new Comment trong trường hợp đang chỉnh sửa comment mà cancel
                    }
                    }>
                        <button
                            type="button"
                            className="w-full text-left text-gray-500 hover:bg-gray-100 rounded-lg text-sm"
                        >
                            Write a comment...
                        </button>
                    </div>
                </div>
            )}

            {isExpanded && (
                <div className="border rounded-md p-3">
                  <Input.TextArea
                    rows={3}
                    placeholder="Comment"
                    value={newComment}
                    onChange={(e) => setNewComment(e.target.value)}
                    className="border-none focus:ring-0 resize-none"
                    autoFocus
                    onKeyDown={(e) => {
                        if (e.key === "Enter" && !e.shiftKey) {
                            e.preventDefault(); // tránh xuống dòng
                            handleSubmit();
                        }
                    }}
                  />
                  <div className="flex justify-between items-center mt-2">
                    <div className="flex gap-3 text-gray-400 text-lg">
                      {/*<PaperClipOutlined />*/}
                      {/*<AudioOutlined />*/}
                      <SmileOutlined />
                      {/*<EnterOutlined />*/}
                    </div>
                    <div className="flex gap-2">
                      <Button onClick={()=> setIsExpanded(false)}>Cancel</Button>
                      <Button type="primary" danger onClick={handleSubmit}>
                        Comment
                      </Button>
                    </div>
                  </div>
                </div>
            )}
        </div>
    );
}
