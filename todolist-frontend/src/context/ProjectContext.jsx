import { createContext, useContext, useState } from "react";

const ProjectContext = createContext();

export const useProjectContext = () => useContext(ProjectContext);

export function ProjectProvider({ children }) {
    const [projects, setProjects] = useState([]);
    const [activeProject, setActiveProject] = useState(null);

    // 👥 members của project
    const [members, setMembers] = useState([]);

    return (
        <ProjectContext.Provider
            value={{ projects, setProjects, activeProject, setActiveProject, members, setMembers }}
        >
            {children}
        </ProjectContext.Provider>
    );
}
