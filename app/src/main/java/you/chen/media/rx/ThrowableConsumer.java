package you.chen.media.rx;


import io.reactivex.functions.Consumer;

/**
 * Created by you on 2017-02-10.
 */
public class ThrowableConsumer implements Consumer<Throwable> {

    @Override
    public void accept(Throwable throwable) throws Exception {
        //nothing
    }

}
