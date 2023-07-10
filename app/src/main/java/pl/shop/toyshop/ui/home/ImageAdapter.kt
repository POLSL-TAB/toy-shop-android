package pl.shop.toyshop.ui.home

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import pl.shop.toyshop.R
import pl.shop.toyshop.model.Picture


class ImageAdapter(private val imageList: ArrayList<Picture>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_container, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val picture = imageList[position]
        holder.imageView.scaleType = ImageView.ScaleType.FIT_CENTER // Ustawienie skali obrazka do FIT_CENTER
        holder.imageView.adjustViewBounds = true // Dopasowanie granic obrazka
        if (picture.pictureB64 != null) {
            val base64ImageData = picture.pictureB64
            val decodedBytes = Base64.decode(base64ImageData, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.imageView.setImageBitmap(bitmap)
        }

        if (position == imageList.size - 1) {
            viewPager2.post(runnable)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    private val runnable = Runnable {
        imageList.addAll(ArrayList(imageList))
        notifyDataSetChanged()
    }
}
