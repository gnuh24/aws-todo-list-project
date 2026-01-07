import { DeleteOutlined } from "@ant-design/icons";
import { BASE_URL, https_media } from "../../service/api";
import { FaDownload } from "react-icons/fa";
import { Modal } from "antd";
import { useState } from "react";

export function CommentAttachItemAdd({ attachments, setAttachments }) {

    const [previewUrl, setPreviewUrl] = useState(null);

    const handleDeleteAttach = async (idFile) => {
        try {
            await https_media.delete(`/v1/local/${idFile}`);
            setAttachments(prev => prev.filter(id => id !== idFile));
        } catch (error) {
            console.error("Delete attach failed:", error);
        }
    };

    const downloadDirect = async (url) => {
        const filename = url.split("/").pop();
        const res = await fetch(url);
        const blob = await res.blob();

        const blobUrl = URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.href = blobUrl;
        link.download = filename;
        document.body.appendChild(link);
        link.click();
        link.remove();
        URL.revokeObjectURL(blobUrl);
    };

    return (
        <>
            <div className="flex flex-wrap gap-2 mt-2 w-full">
                {attachments.map((idFile) => {
                    const fullUrl = `${BASE_URL}/media/v1/local/${idFile}`;

                    return (
                        <div
                            key={idFile}
                            className="w-40 h-40 relative border rounded-lg overflow-hidden bg-gray-50"
                        >
                            {/* IMAGE */}
                            <img
                                src={fullUrl}
                                alt=""
                                className="w-full h-full object-cover cursor-pointer hover:scale-105 transition-transform duration-200"
                                onClick={() => setPreviewUrl(fullUrl)}
                            />

                            {/* ACTION BUTTONS */}
                            <div className="absolute top-2 right-2 flex gap-2">
                                {/* DOWNLOAD */}
                                <button
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        downloadDirect(fullUrl);
                                    }}
                                    className="w-6 h-6 rounded-full bg-white bg-opacity-90 shadow flex items-center justify-center hover:bg-blue-100 transition"
                                    title="Download"
                                >
                                    <FaDownload className="text-gray-700 text-sm" />
                                </button>

                                {/* DELETE */}
                                <button
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleDeleteAttach(idFile);
                                    }}
                                    className="w-6 h-6 rounded-full bg-white bg-opacity-90 shadow flex items-center justify-center hover:bg-red-100 transition"
                                    title="Delete"
                                >
                                    <DeleteOutlined className="text-red-500 text-sm" />
                                </button>
                            </div>
                        </div>
                    );
                })}
            </div>

            {/* MODAL PREVIEW */}
            <Modal
                open={!!previewUrl}
                footer={null}
                onCancel={() => setPreviewUrl(null)}
                width={900}
                closable={false}   // ❌ tắt nút mặc định
                centered
            >
                <img
                    src={previewUrl}
                    alt=""
                    className="w-full max-h-[70vh] object-contain bg-black"
                />


                <div className="flex justify-end mt-3">
                    <button
                        onClick={() => downloadDirect(previewUrl)}
                        className="px-3 py-1 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Download
                    </button>
                </div>
            </Modal>
        </>
    );
}
