import { useEffect, useState } from 'react';


interface User {
  userId: number;
  firstname: string;
  lastname: string;
  email: string;
  phoneNumber: string;
  title: boolean;
  role: Role;
  address: Address;
}

interface Address {
  street: string;
  number: string;
  city: string;
  country: string;
}

interface Role {
  id: number;
  name: string;
}

function ViewAllEmployees() {
  const [users, setUsers] = useState<User[]>([]);

  useEffect(() => {
    fetch('https://localhost:8081/api/v1/user/getAll', {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
      },
      credentials: 'include',
    })
      .then((response) => response.json())
      .then((data) => {
        setUsers(data);
      })
      .catch((error) => console.log(error));
  }, []);

  return (
    <div className="container">
      <h1>Employees</h1>
      <table className="table table-striped">
        <thead className="thead-dark">
          <tr>
            <th>First name</th>
            <th>Last name</th>
            <th>Email</th>
            <th>Phone number</th>
            <th>Title</th>
            <th>Adresa</th>
            <th>Role</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.userId}>
              <td>{user.firstname}</td>
              <td>{user.lastname}</td>
              <td>{user.email}</td>
              <td>{user.phoneNumber}</td>
              <td>{user.title}</td>
              <td>
                {user.address &&
                  `${user.address.street} ${user.address.number}, ${user.address.city}, ${user.address.country}`}
              </td>
              <td>{user.role.name}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ViewAllEmployees;
