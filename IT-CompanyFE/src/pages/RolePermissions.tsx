import { useEffect, useState } from "react";
import "bootstrap/dist/css/bootstrap.min.css";

enum Methods {
  GET_LOGIN_RESPONSE = "GET_LOGIN_RESPONSE",
  GET_ALL_USERS = "GET_ALL_USERS",
  GET_ALL_PROJECTS = "GET_ALL_PROJECTS",
  CREATE_PROJECT = "CREATE_PROJECT",
  GET_USER = "GET_USER",
  UPDATE_USER = "UPDATE_USER",
  ADD_CV ="ADD_CV",
  ADD_SKILL="ADD_SKILL",
  GET_ALL_SKILL = "GET_ALL_SKILL",
  EDIT_SKILL="EDIT_SKILL",
  DOWNLOAD_CV="DOWNLOAD_CV",
  GET_MANAGER_PROJECTS="GET_MANAGER_PROJECTS",
  EDIT_JOB_DESCRIPTION="EDIT_JOB_DESCRIPTION",
  GET_EMPLOYEE_PROJECTS="GET_EMPLOYEE_PROJECTS",
  EDIT_EMPLOYEE_ON_PROJECT="EDIT_EMPLOYEE_ON_PROJECT"
}

enum Roles {
  SOFTWARE_ENGINEER = "SOFTWARE_ENGINEER",
  PROJECT_MANAGER = "PROJECT_MANAGER",
  HR_MANAGER = "HR_MANAGER",
  ADMINISTRATOR = "ADMINISTRATOR"
}

interface Permission {
  id: number;
  role: Roles;
  methods: Methods[];
}

function RolePermissions() {
  const [permissions, setPermissions] = useState<Permission[]>([]);

  useEffect(() => {
    fetch('https://localhost:8081/api/v1/user/permissions', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
      },
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        /* if (data.length === 0) {
          const initialPermissions: Permission[] = [
            {
              id: 1,
              role: Roles.SOFTWARE_ENGINEER,
              methods: []
            },
            {
              id: 2,
              role: Roles.PROJECT_MANAGER,
              methods: []
            },
            {
              id: 3,
              role: Roles.HR_MANAGER,
              methods: []
            },
            {
              id: 4,
              role: Roles.ADMINISTRATOR,
              methods:[]
            }
          ];
          setPermissions(initialPermissions);
        } else { */
          setPermissions(data);
    //    }
      })
      .catch((error) => console.log(error));
  }, []);

  const handlePermissionChange = (roleS: string, method: Methods) => {
    setPermissions((prevPermissions) =>
      prevPermissions.map((permission) => {
        if (permission.role === roleS) {
          const updatedMethods = permission.methods.includes(method)
            ? permission.methods.filter((m) => m !== method)
            : [...permission.methods, method];
          return { ...permission, methods: updatedMethods };
        }
        return permission;
      })
    );
  };

  const savePermissions = () => {
    fetch('https://localhost:8081/api/v1/user/permissions', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(permissions),
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        window.alert("Permissions update success!")
        console.log(data);
      })
      .catch((error) => console.log(error));
  };
  

  return (
    <div className="container">
      <h1 className="mt-4">Role Permissions</h1>
      <div className="row">
        {permissions.map((permission) => (
          <div key={permission.id} className="col-lg-6 mt-4">
            <div className="card">
              <div className="card-header">
                <h2>{permission.role}</h2>
              </div>
              <div className="card-body">
                <ul className="list-group">
                  {Object.values(Methods).map((method) => (
                    <li key={method} className="list-group-item">
                      <div className="form-check">
                        <input
                          className="form-check-input"
                          type="checkbox"
                          checked={permission.methods.includes(method)}
                          onChange={() => handlePermissionChange(permission.role, method)}
                        />
                        <label className="form-check-label">{method}</label>
                      </div>
                    </li>
                  ))}
                </ul>
              </div>
            </div>
          </div>
        ))}
      </div>
      <button className="btn btn-primary mt-4" onClick={savePermissions}>
        Save Permissions
      </button>
    </div>
  );
}

export default RolePermissions;
