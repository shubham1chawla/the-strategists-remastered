module.exports = {
  extends: [
    'airbnb',
    'airbnb-typescript',
    'airbnb/hooks',
    'plugin:prettier/recommended',
  ],
  parser: '',
  parserOptions: {
    project: './tsconfig.json',
  },
  rules: {
    'react/react-in-jsx-scope': 'off',
    'react/require-default-props': 'off',
    'import/extensions': 'off',
    'no-nested-ternary': 'off',
    'import/order': [
      'error',
      {
        groups: ['builtin', 'external', 'internal'],
        pathGroups: [
          // Native React imports first
          {
            pattern: 'react',
            group: 'external',
            position: 'before',
          },
          {
            pattern: 'react-dom',
            group: 'external',
            position: 'before',
          },
          {
            pattern: 'react-dom/**',
            group: 'external',
            position: 'before',
          },
          {
            pattern: 'react-router-dom',
            group: 'external',
            position: 'before',
          },
          // Redux-related imports second
          {
            pattern: '@reduxjs/**',
            group: 'external',
            position: 'before',
          },
          {
            pattern: 'redux',
            group: 'external',
            position: 'before',
          },
          {
            pattern: 'redux-*',
            group: 'external',
            position: 'before',
          },
          {
            pattern: 'react-redux',
            group: 'external',
            position: 'before',
          },
          // Ant Design imports third
          {
            pattern: 'antd',
            group: 'external',
            position: 'before',
          },
          {
            pattern: 'antd/**',
            group: 'external',
            position: 'before',
          },
          {
            pattern: '@ant-design/**',
            group: 'external',
            position: 'before',
          },
          // Anything external
          {
            pattern: '*',
            group: 'external',
            position: 'before',
          },
          // Project aliases
          {
            pattern: '@/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@shared/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@activities/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@advices/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@game/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@login/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@predictions/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@theme/**',
            group: 'internal',
            position: 'after',
          },
          {
            pattern: '@trends/**',
            group: 'internal',
            position: 'after',
          },
        ],
        pathGroupsExcludedImportTypes: [
          'react',
          'react-dom',
          'react-router-dom',
          '@reduxjs',
          'redux',
          'react-redux',
          'antd',
          '@ant-design',
        ],
        'newlines-between': 'never',
        alphabetize: {
          order: 'asc',
          caseInsensitive: true,
        },
      },
    ],
    // Fixing issue with jest & eslint
    'import/no-extraneous-dependencies': [
      'error',
      {
        devDependencies: [
          'test.{ts,tsx}',
          'test-*.{ts,tsx}',
          '**/*{.,_}{test,spec}.{ts,tsx}',
          '**/jest.config.ts',
          '**/jest.setup.ts',
          '**/setupTests.ts',
        ],
        optionalDependencies: false,
      },
    ],
  },
  overrides: [
    // Fixing eslint issue for state update in redux slices
    {
      files: ['./src/**/state.ts'],
      rules: {
        'no-param-reassign': [
          'error',
          {
            props: false,
          },
        ],
      },
    },
  ],
};
