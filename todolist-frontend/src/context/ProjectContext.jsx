import {createContext, useContext, useEffect, useState} from "react";
import {useParams} from "react-router-dom";

const ProjectContext = createContext();

export const useProjectContext = () => useContext(ProjectContext);

export function ProjectProvider({ children }) {

    const { projectId } = useParams();

    const [projects, setProjects] = useState([]);

    const [activeProject, setActiveProject] = useState(null);

    // 👥 members của project
    const [members, setMembers] = useState([]);


    // Section của project
    const [sections, setSections] = useState([]);


    // Xử lý khi user mở chi tiết của một task
    const [activeTaskId, setActiveTaskId] = useState(null);

    const [taskDetail, setTaskDetail] = useState({});

    const [taskStack, setTaskStack] = useState([]);


    return (
        <ProjectContext.Provider
            value={{ projects, setProjects, activeProject, setActiveProject, members, setMembers, sections, setSections, taskDetail,
                setTaskDetail, activeTaskId, setActiveTaskId, taskStack, setTaskStack }}
        >
            {children}
        </ProjectContext.Provider>
    );
}
