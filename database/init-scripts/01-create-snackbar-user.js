db = db.getSiblingDB('snackbar');

db.createUser({
    user: "snackbaruser",
    pwd: "snack01",  
    roles: [{ role: "readWrite", db: "snackbar" }]
});

db.createUser({
  user: "admin",
  pwd: "adminpassword",
  roles: [
    { role: "root", db: "admin" }
  ]
})

print("User 'snackbaruser' created.");