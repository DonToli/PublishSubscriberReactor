package com.example.publishsubscriberreactor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;
@SpringBootApplication
public class PublishSubscriberReactorApplication {

    public static void main(String[] args) throws InterruptedException {
        SpringApplication.run(PublishSubscriberReactorApplication.class, args);

        //Создаю Flux(Publisher)
    ParallelFlux<Object> fluxParallel = Flux.
            create(emitter -> {
        // Опубликовать 100 номеров
        for (int i = 0; i < 100; i++) {
            emitter.next(i);
            // Опубликовать или передать значение с задержкой 10 мс
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Когда все значения появились, вызов завершается.
        emitter.complete();
    })
            //преобразует Flux в ParallelFlux и информирует его о необходимости дальнейшей обработки в другом потоке, т. е. в основном потоке.
            .parallel().runOn(Schedulers.parallel()).map(i -> {//map - обработка вычисления квадратов всех чисел и сопоставление их с ParallelFlux
        int number = (int) i;
        System.out.println(Thread.currentThread().getName() + " | Sending square of " + number);

        // возвести в квадрат все номера и предоставить подписчикам.
        return number * number;
    });

    // Выведем все параллельные потоки в циклическом порядке
        fluxParallel.sequential()
                .subscribe(i -> System.out.println(Thread.currentThread().getName() + " | Received square = " + i));//подписывается на Flux и начинает получать испускаемые значения.
}
}
