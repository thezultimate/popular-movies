package app.dafferianto.info.popularmovies.tasks;

public interface AsyncTaskCompleteListener<T> {

    public void onTaskComplete(T result);

}
