package lilcode.aop.p2c02.lotto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible

class MainActivity : AppCompatActivity() {

    private val clearButton: Button by lazy { // 사용하기 직전에 할당됨
        findViewById<Button>(R.id.clearButton)
    }

    private val addButton: Button by lazy {
        findViewById<Button>(R.id.addButton)
    }

    private val runButton: Button by lazy {
        findViewById<Button>(R.id.runButton)
    }

    private val numberPicker: NumberPicker by lazy {
        findViewById(R.id.numberPicker)
    }

    // 추출 된 로또 번호 6개를 나타낼 텍스트뷰
    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById<TextView>(R.id.textView1),
            findViewById<TextView>(R.id.textView2),
            findViewById<TextView>(R.id.textView3),
            findViewById<TextView>(R.id.textView4),
            findViewById<TextView>(R.id.textView5),
            findViewById<TextView>(R.id.textView6)
        )
    }

    private var didRun = false // 자동 생성 버튼을 최종적으로 누른 경우 true

    private val pickNumberSet = hashSetOf<Int>() // 사용자가 선택한 번호 set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        // 숫자 선택기 범위 설정 (로또 번호 범위로 설정)
        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()
    }

    // 자동 생성 시작 버튼 초기화
    private fun initRunButton() {
        runButton.setOnClickListener {
            didRun = true
            val list = getRandomNumber() // 사용자가 선택한 번호를 포함한 6개 로또 번호 가져오기

            // 인덱스와 값을 사용 하기위해 forEachIndexed
            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]

                textView.text = number.toString()
                textView.isVisible = true

                setNumberBackground(number, textView)
            }
        }
    }

    private fun initAddButton() {
        addButton.setOnClickListener {

            if (didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개 까지만 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호 입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val textView = numberTextViewList[pickNumberSet.size] // 다음 으로 추가될 텍스트 뷰
            textView.isVisible = true
            textView.text = numberPicker.value.toString()

            // 코틀린이라 setBackground 사용 말고 이렇게 사용가능
            // drawable 이 안드로이드 앱에 저장되는 것이기 때문에 Context에서 가져오는 것이 필요
            textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)

            setNumberBackground(numberPicker.value, textView)

            pickNumberSet.add(numberPicker.value) // 선택한 번호 set에 추가
        }
    }

    // 중복을 피하기 위해 함수로 따로 빼서 작성
    private fun setNumberBackground(number: Int, textView: TextView) {
        // number 별 해당하는 배경 설정 drawable 자원 사용
        when (number) {
            in 1..10 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_yellow)
            in 11..20 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_blue)
            in 21..30 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_red)
            in 31..40 ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_gray)
            else ->
                textView.background = ContextCompat.getDrawable(this, R.drawable.circle_green)
        }
    }

    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>().apply {
            for (i in 1..45) {

                // 이미 선택된 번호일 경우 skip
                if (pickNumberSet.contains(i)) {
                    continue
                }

                this.add(i)
            }
            shuffle() // 랜덤으로 셔플
        }

        // 선택한 셋 + 랜덤 번호가 합쳐져서 6개가 되도록 함
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)

        return newList.sorted() // 오름차순으로 정렬하여 반환
    }

    // 초기화 버튼 초기화
    private fun initClearButton() {
        clearButton.setOnClickListener {
            pickNumberSet.clear()
            numberTextViewList.forEach {
                it.isVisible = false
            }
            didRun = false
        }
    }

}