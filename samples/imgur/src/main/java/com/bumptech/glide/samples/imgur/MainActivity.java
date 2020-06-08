package com.bumptech.glide.samples.imgur;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.samples.imgur.api.Image;
import dagger.android.AndroidInjection;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/** Displays images and GIFs from Imgur in a scrollable list of cards. */
public final class MainActivity extends AppCompatActivity {

  @Inject
  @Named("hotViralImages")
  Observable<List<Image>> fetchImagesObservable;

  private ImgurImageAdapter adapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    AndroidInjection.inject(this);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    ImageView imageView = findViewById(R.id.image);
    recyclerView.setHasFixedSize(true);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    recyclerView.setLayoutManager(layoutManager);
    adapter = new ImgurImageAdapter();
    recyclerView.setAdapter(adapter);


//    fetchImagesObservable
//        .subscribeOn(Schedulers.newThread())
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(
//            new Observer<List<Image>>() {
//              @Override
//              public void onCompleted() {}
//
//              @Override
//              public void onError(Throwable e) {
//                Log.w("AAA","assda");
//              }
//
//              @Override
//              public void onNext(List<Image> images) {
//                adapter.setData(images);
//              }
//            });
    ImgurGlide.with(this).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1591291714092&di=3932bbdd2421f5a833cfecef3c34c463&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F14%2F75%2F01300000164186121366756803686.jpg")
        .into(imageView);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    fetchImagesObservable.unsubscribeOn(AndroidSchedulers.mainThread());
  }

  private final class ImgurImageAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<Image> images = Collections.emptyList();

    public void setData(@NonNull List<Image> images) {
      this.images = images;
      notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      return new ViewHolder(
          LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
      ViewHolder vh = (ViewHolder) holder;
      Image image = images.get(position);
      vh.title.setText(TextUtils.isEmpty(image.title) ? image.description : image.title);

      ImgurGlide.with(vh.imageView).load(image.link).into(vh.imageView);
    }

    @Override
    public int getItemCount() {
      return images.size();
    }

    private final class ViewHolder extends RecyclerView.ViewHolder {

      private final ImageView imageView;
      private final TextView title;

      ViewHolder(View itemView) {
        super(itemView);
        imageView = (ImageView) itemView.findViewById(R.id.image);
        title = (TextView) itemView.findViewById(R.id.title);
      }
    }
  }
}
