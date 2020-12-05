sudo apt update && sudo apt upgrade -y
sudo apt install unzip
sudo apt install -y openjdk-8-jre-headless
sudo apt install -y leiningen

wget -c https://istd50043.s3-ap-southeast-1.amazonaws.com/meta_kindle_store.zip -O meta_kindle_store.zip
sudo apt install unzip
unzip meta_kindle_store.zip

sudo apt-get install gnupg
wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add -
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu bionic/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list
sudo apt-get install -y mongodb-org
sudo service mongod start
mongoimport --db kindle --collection metadata --file meta_Kindle_Store.json --legacy
