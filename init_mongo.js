db.createUser({
  user: "monga",
  pwd: "default",
  roles: [{ role: "readWrite", db: "cryptodb" }]
});