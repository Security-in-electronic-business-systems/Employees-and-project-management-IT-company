import { useEffect, useState } from "react";
import { User } from "../model/user";
import '../App.css';

function ViewRegistrationRequests(){
    const [users,setUsers] = useState<User[]>([]);
    const [showPopup, setShowPopup] = useState(false);
    const [rejectionReason, setRejectionReason] = useState("");
    const [selectedUser, setSelectedUser] = useState<User | null>(null);
  

    useEffect(() => {
        fetch('https://localhost:8081/api/v1/user/registration/requests', {
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

    const accept = async (user: User) => {
        setSelectedUser(user);
        submitPopup(user, true);
    };

    const decline = async (user: User) => {
        setSelectedUser(user);
        setShowPopup(true);
    };

    const submitPopup = async(user:User, tmp : boolean) => {
        // Implement submit logic here
        if (tmp == true){
            await fetch("https://localhost:8081/api/v1/user/registration/accept", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify({
                  "email": user?.email,
                }),
              })
                .then((response) => {
                  console.log(response)
                  setShowPopup(false);
                  setRejectionReason("");
                  setSelectedUser(null);
                })
                .catch((error) => {
                    console.log(error);
                    setShowPopup(false);
                    setRejectionReason("");
                    setSelectedUser(null);
                });
        }else{
            await fetch("https://localhost:8081/api/v1/user/registration/decline", {
                method: "POST",
                headers: {
                  "Content-Type": "application/json",
                },
                credentials: "include",
                body: JSON.stringify({
                  "description":rejectionReason,
                  "email": selectedUser?.email,
                }),
              })
                .then((response) => {
                  console.log(response)
                  setShowPopup(false);
                  setRejectionReason("");
                  setSelectedUser(null);
                })
                .catch((error) => {
                    console.log(error);
                    setShowPopup(false);
                    setRejectionReason("");
                    setSelectedUser(null);
                });
        }
    };

    const cancelPopup = () => {
        setShowPopup(false);
        setRejectionReason("");
        setSelectedUser(null);
    };

    return (
        <div>
          <blockquote className="blockquote text-center">
            <p className="mb-0">All registration requests:</p>
          </blockquote>
      
          <div>
            <table className="table table-striped" style={{width: '100%', alignItems:"center", marginLeft : "auto", marginRight:  "auto"}}>
              <thead className="thead-dark">
                <tr>
                  <th scope="col">Name</th>
                  <th scope="col">Surname</th>
                  <th scope="col">Email</th>
                  <th scope="col">Phone number</th>
                  <th scope="col">Title</th>
                  <th scope="col">Role</th>
                  <th scope="col"></th>
                  <th scope="col"></th>
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.email}>
                    <td>{user.firstname}</td>
                    <td>{user.lastname}</td>
                    <td>{user.email}</td>
                    <td>{user.phoneNumber}</td>
                    <td>{user.title}</td>
                    <td>{user.role?.name}</td>
                    <td>
                      <button
                        type="submit"
                        className="btn btn-primary"
                        onClick={() => accept(user)}
                      >
                        Accept
                      </button>
                    </td>
                    <td>
                      <button
                        type="submit"
                        className="btn btn-primary"
                        onClick={() => decline(user)}
                      >
                        Decline
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          {showPopup && selectedUser && (
            <div className="popup">
              <div className="popup-content">
                <h2>Reason for Rejection</h2>
                <textarea
                  value={rejectionReason}
                  onChange={(e) => setRejectionReason(e.target.value)}
                  placeholder="Enter the reason for rejection..."
                ></textarea>
                <div className="popup-buttons">
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={() => submitPopup(selectedUser, false)}
                  >
                    Submit
                  </button>
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cancelPopup}
                  >
                    Cancel
                  </button>
                </div>
              </div>
            </div>
          )}
        </div>
      );
      
      
}

export default ViewRegistrationRequests;