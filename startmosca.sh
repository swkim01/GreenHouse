sudo npm install

basedir=`dirname $0`
mosca=$basedir/node_modules/mosca/bin/mosca
bunyan=$basedir/node_modules/mosca/node_modules/.bin/bunyan

$mosca -v | $bunyan

 
