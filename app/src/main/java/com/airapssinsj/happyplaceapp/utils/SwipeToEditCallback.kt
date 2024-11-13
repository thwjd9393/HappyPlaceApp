package pl.kitek.rvswipetodelete

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.airapssinsj.happyplaceapp.R

abstract class SwipeToEditCallback(context: Context)
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
//    companion object {
//        var direction = 0
//        var _direction = if(direction==0) ItemTouchHelper.RIGHT else ItemTouchHelper.LEFT
//    }
//ItemTouchHelper.RIGHT : 어느 방향으로 스와이프 하고싶은지
    
    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_edit_white_24dp)
    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp)

    private val intrinsicWidth = editIcon!!.intrinsicWidth
    private val intrinsicHeight = editIcon!!.intrinsicHeight

    private val intrinsicWidthDel = deleteIcon!!.intrinsicWidth
    private val intrinsicHeightDel = deleteIcon!!.intrinsicHeight
    
    private val background = ColorDrawable()
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }


    //스와이프 기능에 제한 둔 부분
    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        /**
         * To disable "swipe" for specific item return 0 here.
         * For example:
         * if (viewHolder?.itemViewType == YourAdapter.SOME_TYPE) return 0
         * if (viewHolder?.adapterPosition == 0) return 0
         */
        if (viewHolder.adapterPosition == 10) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    //모든 것을 한번에 움직이는 메소드
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onChildDraw(
            c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
    ) {

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top
        val isCanceled = dX == 0f && !isCurrentlyActive

        if (isCanceled) {
            clearCanvas(c, itemView.left + dX, itemView.top.toFloat(), itemView.left.toFloat(), itemView.bottom.toFloat())
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        // Draw the green edit background
        if (dX > 0) { // 오른쪽으로 스와이프 (수정)
            background.color = Color.parseColor("#24AE05")
            background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
            background.draw(c)

            // 수정 아이콘 위치 계산 및 그리기
            val editIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
            val editIconLeft = itemView.left + 16 // 아이콘을 좀 더 안쪽으로 위치
            val editIconRight = editIconLeft + intrinsicWidth
            val editIconBottom = editIconTop + intrinsicHeight
            editIcon!!.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom)
            editIcon.draw(c)
        } else if (dX < 0) { // 왼쪽으로 스와이프 (삭제)
            background.color = Color.parseColor("#F44336")
            background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
            background.draw(c)

            // 삭제 아이콘 위치 계산 및 그리기
            val deleteIconTop = itemView.top + (itemHeight - intrinsicHeightDel) / 2
            val deleteIconRight = itemView.right - 16 // 아이콘을 좀 더 안쪽으로 위치
            val deleteIconLeft = deleteIconRight - intrinsicWidthDel
            val deleteIconBottom = deleteIconTop + intrinsicHeightDel
            deleteIcon!!.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
            deleteIcon.draw(c)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }
}
// END