# TrafficProject

This project is realized by SpringBoot and aim to improve the e-commerce site performance using Redis and RabbitMQ or Kafka

## Technology used

>Redis 
   - Create our distributed session management
   - Cache the static page like goods_detail.htm to speed up the page loading, must use with ajax
   - Take advantage of redis' single thread (thread safe) to keep the stock modification to be automicity


>RabbitMQ : 
   - Reduce our server stress
   
>Jmeter : 

   - Test the performance
