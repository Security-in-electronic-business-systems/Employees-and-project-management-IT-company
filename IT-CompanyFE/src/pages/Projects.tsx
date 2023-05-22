import { useEffect, useState } from 'react';
import 'bootstrap/dist/css/bootstrap.min.css';
import Table from 'react-bootstrap/Table';

interface Project {
  id: number;
  name: string;
  description: string;
  startDate: string;
  endDate: string;
  employeeProjects: EmployeeProject[];
}

interface EmployeeProject {
  id: number;
  jobDescription: string;
  startDate: string;
  endDate: string;
  user: {
    id: number;
    firstname: string;
    lastname: string;
  };
}

function Projects ()  {
  const [projects, setProjects] = useState<Project[]>([]);

  useEffect(() => {
    fetch('https://localhost:8081/api/v1/user/getAllProjects', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
      },
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        setProjects(data);
      })
      .catch((error) => console.log(error));
  }, []);

  const renderEmployees = (employees: EmployeeProject[]) => {
    return employees.map((employee) => (
      <tr key={employee.id}>
        <td>{employee.user.firstname} {employee.user.lastname}</td>
        <td>{employee.jobDescription}</td>
        <td>{new Date(employee.startDate).toLocaleDateString()}</td>
        <td>{new Date(employee.endDate).toLocaleDateString()}</td>
      </tr>
    ));
  };

  const renderProjects = () => {
    return projects.map((project) => (
      <div key={project.id}>
        <h2>{project.name}</h2>
        <p>{project.description}</p>
        <p>Duration: {new Date(project.startDate).toLocaleDateString()} - {new Date(project.endDate).toLocaleDateString()}</p>
        <Table striped bordered hover>
          <thead>
            <tr>
              <th>Name</th>
              <th>Job Description</th>
              <th>Start Date</th>
              <th>End Date</th>
            </tr>
          </thead>
          <tbody>
            {renderEmployees(project.employeeProjects)}
          </tbody>
        </Table>
      </div>
    ));
  };

  return (
    <div>
      <h1>Projects</h1>
      {renderProjects()}
    </div>
  );
}

export default Projects;
