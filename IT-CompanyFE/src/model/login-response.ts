export class LoginResponse {
    userId: number = 0;
    firstname: string = '';
    lastname: string = '';
    email: string = '';
    phoneNumber: string = '';
    title: string = '';
    address: Address = new Address();
    role: Role = new Role();
    message: string = '';
  }
  
  export class Address {
    country: string = '';
    city: string = '';
    street: string = '';
    number: string = '';
  
    constructor(country: string = '', city: string = '', street: string = '', number: string = '') {
      this.country = country;
      this.city = city;
      this.street = street;
      this.number = number;
    }
  }
  
  export class Role {
    id: number = 0;
    name: string = '';
  }
  