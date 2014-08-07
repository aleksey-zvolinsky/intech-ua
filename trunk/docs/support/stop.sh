ps aux | grep intechua | grep java | grep server | awk {'print $2'} |xargs kill -15
sleep 5
ps aux | grep intechua | grep java | grep server | awk {'print $2'} |xargs kill -9