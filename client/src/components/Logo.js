import React from 'react'

function Logo() {
  return (
    <div className='w-7 h-6 mt-1 rounded-md bg-green-500'>
        <div className='w-full grid grid-rows-2 grid-cols-1 gap-1 p-1'>
            <div className='w-full flex items-center justify-end'>
                <div className='w-1 h-1 p-1 rounded-full bg-white'></div>
            </div>
            <div className='w-full h-1 bg-white rounded-md'></div>
        </div>
    </div>
  )
}

export default Logo