package advent.util

import advent.y2024.Day15
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer

class Day15Application(private val day15: Day15) : ApplicationAdapter() {

    private lateinit var shapeRenderer: ShapeRenderer
    private var paused = false
    override fun create() {
        shapeRenderer = ShapeRenderer()
    }

    override fun render() {
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            paused = true
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            paused = false
        }
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled)
        day15.widerCharMap.forEach { (coord, char) ->
            shapeRenderer.color = when (char) {
                '#' -> Color.GRAY
                '[', ']' -> Color.BLUE
                '@' -> Color.GREEN
                else -> Color.WHITE
            }
            shapeRenderer.rect(coord.first * CELL_SIZE, coord.second * CELL_SIZE, CELL_SIZE, CELL_SIZE)
        }
        shapeRenderer.end()
        if (!paused) {
            day15.moveRobotInWiderMap()
        }
    }

    override fun dispose() {
        shapeRenderer.dispose()
    }

}