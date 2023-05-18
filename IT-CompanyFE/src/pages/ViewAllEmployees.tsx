import { useEffect, useState } from 'react';

interface User {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
  phoneNumber: string;
  isApproved: boolean;
  title: string;
  address: Address;
}

interface Address {
  street: string;
  number: string;
  city: string;
  country: string;
}

function ViewAllEmployees() {
  const [users, setUsers] = useState<User[]>([]);

  useEffect(() => {
    fetch('https://localhost:8081/api/v1/auth/getAll',{
      method: "GET",
      credentials: "include",})
      .then((response) => response.json())
      .then((data) => {
        setUsers(data);
      })
      .catch((error) => console.log(error));
  }, []);

  return (
    <div>
      <h1>Prikaz korisnika</h1>
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>Ime</th>
            <th>Prezime</th>
            <th>Email</th>
            <th>Telefon</th>
            <th>Odobren</th>
            <th>Naslov</th>
            <th>Adresa</th>
          </tr>
        </thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.firstname}</td>
              <td>{user.lastname}</td>
              <td>{user.email}</td>
              <td>{user.phoneNumber}</td>
              <td>{user.isApproved ? 'Da' : 'Ne'}</td>
              <td>{user.title}</td>
              <td>
                {user.address &&
                  `${user.address.street} ${user.address.number}, ${user.address.city}, ${user.address.country}`}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default ViewAllEmployees;
