export class LoginResponse {
  userId = 0;
  firstname=  '';
  lastname = '';
  email = '';
  phoneNumber = '';
  title = '';
  address = new Address();
  role = new Role();
  message = '';
}

export class Address {
  country = '';
  city = '';
  street = '';
  number = '';
}

export class Role {
  id = 0;
  name = '';
}
